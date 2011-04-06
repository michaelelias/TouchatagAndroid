package com.touchatag.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings.Secure;
import android.view.Window;
import android.widget.Toast;

import com.touchatag.android.R;
import com.touchatag.android.TouchatagApplication;
import com.touchatag.android.client.CorrelationGateway;
import com.touchatag.android.client.soap.command.TagEventCommand;
import com.touchatag.android.client.soap.model.common.ClientId;
import com.touchatag.android.client.soap.model.common.GenericTagType;
import com.touchatag.android.client.soap.model.common.ReaderId;
import com.touchatag.android.client.soap.model.common.TagEventType;
import com.touchatag.android.client.soap.model.common.TagId;
import com.touchatag.android.client.soap.model.common.TagInfo;
import com.touchatag.android.client.soap.model.request.TagEvent;
import com.touchatag.android.client.soap.model.response.Container;
import com.touchatag.android.client.soap.model.response.LegacyClientActionResponse;
import com.touchatag.android.client.soap.model.response.TagEventFeedback;
import com.touchatag.android.client.soap.serialization.ApplicationResponseDeserializer.ApplicationResponseType;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.store.TagEventCommandStore;
import com.touchatag.android.tag.mifare.ultralight.CouldNotReadTagDataException;
import com.touchatag.android.tag.mifare.ultralight.MifareUltralightTagHandler;
import com.touchatag.android.util.HexFormatter;
import com.touchatag.android.util.NdefMessageParser;
import com.touchatag.android.util.NotificationUtils;

public class ClientActivity extends BaseActivity {

	private static final int DIALOG_PROBLEM = 1;
	private static final int DIALOG_TAGACTION = 2;
	private static final int DIALOG_NOTGENUINE = 3;
	private static final int DIALOG_TAGACTION_ERROR = 4;

	private String problemTitle;
	private String problemDescription;

	private MifareUltralightTagHandler tagHandler;
	private CorrelationGateway ttGateway;
	private SettingsStore settingsStore;
	private TagEventCommandStore tagEventCommandStore;

	private ProgressDialog progressDialog;

	private ClientStatus status;

	private Intent resultIntent;

	private enum ClientStatus {
		READING_TAGDATA, SENDING_TAGEVENT, TAGEVENT_RESPONSE_RECEIVED, NOT_GENUINE_TAG, NO_TAG_NEAR_PHONE, NO_TAGDATA
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.client);
		settingsStore = new SettingsStore(this);
		tagEventCommandStore = new TagEventCommandStore(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (tagHandler != null) {
			tagHandler.release();
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Uri uri = extractUriFromIntent(getIntent());

		if (uri == null) {
			setStatus(ClientStatus.NOT_GENUINE_TAG);
			return;
		}
		tagHandler = new MifareUltralightTagHandler(tag);

		byte[] tagData = null;
		try {
			tagData = tagHandler.readAll();
		} catch (CouldNotReadTagDataException e) {
			setStatus(ClientStatus.NO_TAG_NEAR_PHONE);
			tagHandler.release();
			return;
		}
		if (tagData.length == 0) {
			setStatus(ClientStatus.NO_TAGDATA);
			tagHandler.release();
			return;
		}

		ttGateway = new CorrelationGateway(settingsStore.getUsername(), settingsStore.getPassword(), settingsStore.getServerEndpoint());
		fireTagEvent(tagData);

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private Parcelable extractTagFromIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			return intent.getParcelableExtra("android.nfc.extra.TAG");
		}
		return null;
	}

	private Uri extractUriFromIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			NdefMessage msg = null;
			for (int i = 0; i < rawMsgs.length; i++) {
				Uri uri = NdefMessageParser.parseTouchatagURI((NdefMessage) rawMsgs[i]);
				if (uri != null) {
					return uri;
				}
			}
		}
		return null;
	}

	private byte[] readTagData() throws CouldNotReadTagDataException {
		return tagHandler.readAll();
	}

	private void fireTagEvent(byte[] tagData) {

		ClientId clientId = new ClientId();
		String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		clientId.setId(androidId);
		clientId.setName(settingsStore.getClientName());

		ReaderId readerId = new ReaderId(androidId.getBytes(), androidId);

		TagId tagId = new TagId();
		tagId.setGenericTagType(GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT);
		tagId.setIdentifier("0x" + HexFormatter.toHexString(tagHandler.getTagUID()));

		TagInfo actionTag = new TagInfo(tagId, tagData);

		TagEvent tagEvent = new TagEvent();
		tagEvent.setClientId(clientId);
		tagEvent.setReaderId(readerId);
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setActionTag(actionTag);

		new SendTagEventToServerAsyncTask().execute(tagEvent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROBLEM:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(problemTitle) //
					.setIcon(android.R.drawable.ic_dialog_alert) //
					.setMessage(problemDescription) //
					.setCancelable(false) //
					.setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							finish();
						}
					});
			return builder.create();
		case DIALOG_TAGACTION:
			String message = "Do you want to execute this tag action?\n\nAction :\tOpen URL\n";
			message += "Content :\t" + resultIntent.getData().toString();

			builder = new AlertDialog.Builder(this);
			builder.setTitle("Tag Action") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage(message) //
					.setCancelable(true) //
					.setPositiveButton("Execute action", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							launchTagActionIntent(resultIntent);
						}
					}).setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							finish();
						}
					});
			return builder.create();
		case DIALOG_NOTGENUINE:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Not a genuine tag") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage("Only genuine Touchatag tags can be used by this application. \n\nWould you like to buy some in our e-store?") //
					.setCancelable(true) //
					.setPositiveButton("Go to e-store", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.touchatag.com/e-store"));
							startActivity(intent);
							finish();
						}
					}).setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							finish();
						}
					});
			return builder.create();
		case DIALOG_TAGACTION_ERROR:
			if (resultIntent != null) {
				message = "Something went wrong launching the tag action.\n\nAction :\tOpen URL\n";
				message += "Content :\t" + resultIntent.getData().toString();
			} else {
				message = "Something went wrong launching the tag action. Try again";
			}
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops!") //
					.setIcon(android.R.drawable.ic_dialog_alert) //
					.setMessage(message) //
					.setCancelable(true) //
					.setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							finish();
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private void showProblemDialog(String title, String description) {
		problemTitle = title;
		problemDescription = description;
		showDialog(DIALOG_PROBLEM);
	}

	private void showNotGenuineDialog() {
		showDialog(DIALOG_NOTGENUINE);
	}

	private void launchTagActionIntent(Intent intent) {
		try {
			startActivity(intent);
			finish();
		} catch (ActivityNotFoundException e) {
			showDialog(DIALOG_TAGACTION_ERROR);
		}
	}

	private void setStatus(ClientStatus status) {
		switch (status) {
		case READING_TAGDATA:
			break;
		case SENDING_TAGEVENT:
			break;
		case NOT_GENUINE_TAG:
			showNotGenuineDialog();
			break;
		case NO_TAG_NEAR_PHONE:
			showProblemDialog("No tag near phone", "You moved your phone away too fast from the tag. The application needs to be able to read additional data from the tag.");
			break;
		case NO_TAGDATA:
			showProblemDialog("No data on tag", "The tag did not contain the expected data. Maybe your Touchatag tag is broken.");
			break;
		case TAGEVENT_RESPONSE_RECEIVED:
		}
	}

	private void processResponse(TagEventFeedback feedback) {
		if (feedback == null) {
			NotificationUtils.showFeedbackMessage(this, "Oops! something went wrong! Try again.");
			finish();
		}
		if (feedback.getSystemMessage() != null) {
			NotificationUtils.showFeedbackMessage(this, feedback.getSystemMessage());
			finish();
		} else if (feedback.getApplicationResponses().size() > 0) {
			if (feedback.getApplicationResponses().containsKey(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier())) {
				LegacyClientActionResponse appResponse = (LegacyClientActionResponse) feedback.getApplicationResponses().get(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier());
				Container con = appResponse.getClientAction().getContainer();
				if (Container.TAG_MANAGEMENT.equalsIgnoreCase(con.getName())) {
					String tagReconfigMessage = con.getContainer().getAttributes().get(Container.ATTR_MESSAGE);
					NotificationUtils.showFeedbackMessage(this, tagReconfigMessage);
					finish();
				} else if (Container.URL.equalsIgnoreCase(con.getName())) {
					String url = con.getContainer().getAttributes().get(Container.ATTR_URL);
					Intent intent = intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					if (settingsStore.isAutoLaunch()) {
						launchTagActionIntent(intent);
					} else {
						resultIntent = intent;
						showDialog(DIALOG_TAGACTION);
					}
				}
			}
		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, "", "Contacting Touchatag server", true);
		} else {
			progressDialog.show();
		}
	}

	private void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private class SendTagEventToServerAsyncTask extends AsyncTask<TagEvent, Void, TagEventCommand> {

		@Override
		protected void onPreExecute() {
			setStatus(ClientStatus.SENDING_TAGEVENT);
			showProgressDialog();
		}

		@Override
		protected TagEventCommand doInBackground(TagEvent... params) {
			return ttGateway.handleTagEvent(params[0]);
		}

		@Override
		protected void onPostExecute(TagEventCommand command) {
			hideProgressDialog();
			setStatus(ClientStatus.TAGEVENT_RESPONSE_RECEIVED);

			if (TouchatagApplication.FULL) {
				tagEventCommandStore.store(command);
			}

			switch (command.getResponseHttpStatusCode()) {
			case 0:
				NotificationUtils.showFeedbackMessage(ClientActivity.this, "Connection timed out.");
				finish();
				break;
			case 500:
				NotificationUtils.showFeedbackMessage(ClientActivity.this, "The server encountered an error, please try again.");
				finish();
				break;
			case 401:
				NotificationUtils.showFeedbackMessage(ClientActivity.this, "Your Touchatag username or password is not correct");
				finish();
				break;
			case 200:
				break;
			}

			processResponse(command.getResponse());
		}

	}

}
