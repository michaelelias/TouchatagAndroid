package com.touchatag.android.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Window;

import com.touchatag.android.R;
import com.touchatag.android.client.CorrelationGateway;
import com.touchatag.android.client.TouchatagRestClient;
import com.touchatag.android.client.rest.model.AcsApiException;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.client.soap.command.TagEventCommand;
import com.touchatag.android.client.soap.model.common.ClientId;
import com.touchatag.android.client.soap.model.common.GenericTagType;
import com.touchatag.android.client.soap.model.common.ReaderId;
import com.touchatag.android.client.soap.model.common.TagEventType;
import com.touchatag.android.client.soap.model.common.TagId;
import com.touchatag.android.client.soap.model.common.TagInfo;
import com.touchatag.android.client.soap.model.request.TagEvent;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.store.TagStore;
import com.touchatag.android.tag.mifare.ultralight.CouldNotReadTagDataException;
import com.touchatag.android.tag.mifare.ultralight.MifareUltralightTagHandler;
import com.touchatag.android.util.HexFormatter;
import com.touchatag.android.util.NotificationUtils;

public class ClaimTagActivity extends Activity {

	public static final String EXTRA_TAG_IDENTIFIER = "tag.identifier";
	
	private SettingsStore settingsStore;
	private TagStore tagStore;

	private boolean acquiring = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.claim_tag);

		settingsStore = new SettingsStore(this);
		tagStore = new TagStore(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!acquiring) {
			enableForegroundDispatching();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableForegroundDispatching();
	}

	private void onTagAcquired(Tag tag) {
		tagStore.store(tag);
		Intent intent = new Intent();
		intent.putExtra(EXTRA_TAG_IDENTIFIER, tag.getIdentifier());
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onNewIntent(Intent intent) {
		android.nfc.Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		MifareUltralightTagHandler tagHandler = new MifareUltralightTagHandler(tag);

		byte[] tagData = null;
		try {
			tagData = tagHandler.readAll();
		} catch (CouldNotReadTagDataException e) {
			tagHandler.release();
			return;
		}
		if (tagData.length == 0) {
			tagHandler.release();
			return;
		}

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

		AcquireTagAsyncTask acquireTagTask = new AcquireTagAsyncTask();
		acquireTagTask.execute();

		SendTagEventAsyncTask task = new SendTagEventAsyncTask();
		task.execute(tagEvent);
	}

	private void enableForegroundDispatching() {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		IntentFilter[] intentFiltersArray = new IntentFilter[] { ndef };
		String[][] techFilters = new String[1][3];
		techFilters[0][0] = "android.nfc.tech.NfcA";
		techFilters[0][1] = "android.nfc.tech.Ndef";
		techFilters[0][2] = "android.nfc.tech.MifareUltralight";
		NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techFilters);
	}

	private void disableForegroundDispatching() {
		NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
	}

	private class AcquireTagAsyncTask extends AsyncTask<Void, Void, Tag> {

		private ProgressDialog progressDialog;
		private TouchatagRestClient restClient;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
			progressDialog = ProgressDialog.show(ClaimTagActivity.this, null, "Acquiring Tag...", true, true, new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// restClient.
					progressDialog.dismiss();
				}
			});
		}

		@Override
		protected Tag doInBackground(Void... params) {
			try {
				acquiring = true;
				return restClient.acquireTag(settingsStore.getClientName(), 6000);
			} catch (AcsApiException e) {
				e.printStackTrace();
				return null;
			} finally {
				acquiring = false;
			}
		}

		@Override
		protected void onPostExecute(Tag tag) {
			super.onPostExecute(tag);
			progressDialog.dismiss();
			if (tag == null) {
				NotificationUtils.showFeedbackMessage(ClaimTagActivity.this, "Failed to claim tag, try again.");
			} else {
				NotificationUtils.showFeedbackMessage(ClaimTagActivity.this, "Claimed tag " + tag.getIdentifier());
				onTagAcquired(tag);
			}
		}
	}

	private class SendTagEventAsyncTask extends AsyncTask<TagEvent, Void, TagEventCommand> {

		@Override
		protected TagEventCommand doInBackground(TagEvent... params) {
			CorrelationGateway gateway = new CorrelationGateway(settingsStore.getUsername(), settingsStore.getPassword(), settingsStore.getServerEndpoint());
			return gateway.handleTagEvent(params[0]);
		}

	}
}
