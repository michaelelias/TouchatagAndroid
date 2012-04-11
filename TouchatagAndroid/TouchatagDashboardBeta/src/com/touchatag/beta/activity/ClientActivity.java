package com.touchatag.beta.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings.Secure;
import android.view.Window;

import com.touchatag.beta.R;
import com.touchatag.beta.TouchatagApplication;
import com.touchatag.beta.activity.template.Template;
import com.touchatag.beta.activity.template.TouchatagTemplate;
import com.touchatag.beta.client.CorrelationGateway;
import com.touchatag.beta.client.soap.command.TagEventCommand;
import com.touchatag.beta.client.soap.model.common.ClientId;
import com.touchatag.beta.client.soap.model.common.GenericTagType;
import com.touchatag.beta.client.soap.model.common.ReaderId;
import com.touchatag.beta.client.soap.model.common.TagEventType;
import com.touchatag.beta.client.soap.model.common.TagId;
import com.touchatag.beta.client.soap.model.common.TagInfo;
import com.touchatag.beta.client.soap.model.request.TagEvent;
import com.touchatag.beta.client.soap.model.response.AndroidClientActionResponse;
import com.touchatag.beta.client.soap.model.response.Container;
import com.touchatag.beta.client.soap.model.response.LegacyClientActionResponse;
import com.touchatag.beta.client.soap.model.response.TagEventFeedback;
import com.touchatag.beta.client.soap.serialization.ApplicationResponseDeserializer.ApplicationResponseType;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.store.TagEventCommandStore;
import com.touchatag.beta.tag.mifare.ultralight.CouldNotReadTagDataException;
import com.touchatag.beta.tag.mifare.ultralight.MifareUltralightTagHandler;
import com.touchatag.beta.util.HexFormatter;
import com.touchatag.beta.util.NdefMessageParser;
import com.touchatag.beta.util.NotificationUtils;
import com.touchatag.beta.util.Utils;

public class ClientActivity extends Activity {

	private static final int DIALOG_PROBLEM = 1;
	private static final int DIALOG_TAGACTION = 2;
	private static final int DIALOG_NOTGENUINE = 3;
	private static final int DIALOG_TAGACTION_ERROR = 4;
	private static String URL_PREFIX = "http://www.ttag.be/m/";

	private String problemTitle;
	private String problemDescription;

	private MifareUltralightTagHandler tagHandler;
	private CorrelationGateway ttGateway;
	private SettingsStore settingsStore;
	private TagEventCommandStore tagEventCommandStore;

	private ClientStatus status;

	private Intent resultIntent;

	private enum ClientStatus {
		READING_TAGDATA, SENDING_TAGEVENT, TAGEVENT_RESPONSE_RECEIVED, NOT_GENUINE_TAG, NO_TAG_NEAR_PHONE, NO_TAGDATA
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = getIntent();
		String tagUID = "";
		byte[] tagData = new byte[0];
		if(intent.getAction().equals(Intent.ACTION_VIEW)){
			tagUID = intent.getData().toString();
			tagUID = tagUID.substring(URL_PREFIX.length());
		} else if(intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
			Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Uri uri = extractUriFromIntent(getIntent());
	
			if (uri == null) {
				// Disabled to allow more generic tags ti be used for the BETA
				//setStatus(ClientStatus.NOT_GENUINE_TAG);
				//return;
			}
			tagHandler = new MifareUltralightTagHandler(tag);
			tagUID = "0x" + HexFormatter.toHexString(tagHandler.getTagUID());
			
//			try {
//				tagData = tagHandler.readAll();
//			} catch (CouldNotReadTagDataException e) {
				//setStatus(ClientStatus.NO_TAG_NEAR_PHONE);
//				tagHandler.release();
				//return;
//			}
		}
//		if (tagData.length == 0) {
//			setStatus(ClientStatus.NO_TAGDATA);
//			tagHandler.release();
//			return;
//		}

		ttGateway = new CorrelationGateway(settingsStore.getUsername(), settingsStore.getPassword(), settingsStore.getServer());
		fireTagEvent(tagUID, tagData);

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

	private void fireTagEvent(String tagUID, byte[] tagData) {

		ClientId clientId = new ClientId();
		String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		clientId.setId(androidId);
		clientId.setName(settingsStore.getClientName());

		ReaderId readerId = new ReaderId(androidId.getBytes(), androidId);

		TagId tagId = new TagId();
		tagId.setGenericTagType(GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT);
		tagId.setIdentifier(tagUID);

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
			return;
		}
		if (feedback.getSystemMessage() != null) {
			NotificationUtils.showFeedbackMessage(this, feedback.getSystemMessage());
			finish();
			return;
		} else if (feedback.getApplicationResponses().size() > 0) {
			if (feedback.getApplicationResponses().containsKey(ApplicationResponseType.ANDROID_CLIENT_ACTION.getIdentifier())) {
				AndroidClientActionResponse appResponse = (AndroidClientActionResponse) feedback.getApplicationResponses().get(ApplicationResponseType.ANDROID_CLIENT_ACTION.getIdentifier());
				String templateId = appResponse.getParameters().get(Template.IDENTIFIER);
				TouchatagTemplate template = TouchatagTemplate.getTemplateByIdentifier(templateId);
				if(template == null){
					NotificationUtils.showFeedbackMessage(this, "Error resolving template from app response. Cannot find template with identifier " + templateId);
					finish();
					return;
				}
				template.execute(appResponse, this);
				finish();
				return;
			}
			else if (feedback.getApplicationResponses().containsKey(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier())) {
				LegacyClientActionResponse appResponse = (LegacyClientActionResponse) feedback.getApplicationResponses().get(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier());
				Container con = appResponse.getClientAction().getContainer();
				if (Container.TAG_MANAGEMENT.equalsIgnoreCase(con.getName())) {
					String tagReconfigMessage = con.getContainer().getAttributes().get(Container.ATTR_MESSAGE);
					NotificationUtils.showFeedbackMessage(this, tagReconfigMessage);
					finish();
					return;
				} else if (Container.URL.equalsIgnoreCase(con.getName())) {
					String uri = con.getContainer().getAttributes().get(Container.ATTR_URL);
					uri = Utils.decodeAmpersant(uri);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					if (settingsStore.isAutoLaunch()) {
						launchTagActionIntent(intent);
					} else {
						resultIntent = intent;
						showDialog(DIALOG_TAGACTION);
					}
				}
			}
		} else {
			NotificationUtils.showFeedbackMessage(this, "Oops! Something went wrong! Try again.");
			finish();
		}
	}

	
	
	private class SendTagEventToServerAsyncTask extends AsyncTask<TagEvent, Void, TagEventCommand> {

		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			setStatus(ClientStatus.SENDING_TAGEVENT);
			progressDialog = ProgressDialog.show(ClientActivity.this, "", "Contacting Touchatag server", true, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					progressDialog.dismiss();
					NotificationUtils.showFeedbackMessage(ClientActivity.this, "Cancelled Touchatag lookup");
					ClientActivity.this.finish();
				}
			});
		}

		@Override
		protected TagEventCommand doInBackground(TagEvent... params) {
			return ttGateway.handleTagEvent(params[0]);
		}

		@Override
		protected void onPostExecute(TagEventCommand command) {
			progressDialog.dismiss();
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
