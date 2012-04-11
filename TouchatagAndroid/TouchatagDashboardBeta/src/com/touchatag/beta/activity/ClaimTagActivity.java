package com.touchatag.beta.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.Window;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.TagApiClient;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.client.CorrelationGateway;
import com.touchatag.beta.client.soap.command.TagEventCommand;
import com.touchatag.beta.client.soap.model.common.ClientId;
import com.touchatag.beta.client.soap.model.common.GenericTagType;
import com.touchatag.beta.client.soap.model.common.ReaderId;
import com.touchatag.beta.client.soap.model.common.TagEventType;
import com.touchatag.beta.client.soap.model.common.TagId;
import com.touchatag.beta.client.soap.model.common.TagInfo;
import com.touchatag.beta.client.soap.model.request.TagEvent;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.store.TagStore;
import com.touchatag.beta.tag.mifare.ultralight.MifareUltralightTagHandler;
import com.touchatag.beta.util.HexFormatter;
import com.touchatag.beta.util.NotificationUtils;

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

		byte[] tagData = new byte[0];
//		try {
//			tagData = tagHandler.readAll();
//		} catch (CouldNotReadTagDataException e) {
//			tagHandler.release();
//			return;
//		}
//		if (tagData.length == 0) {
//			tagHandler.release();
//			return;
//		}

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

		AcquireTagAsyncTask acquireTagTask = new AcquireTagAsyncTask("Claiming Tag...", "Failed to claim tag. Maybe it's already claimed by another user or it has been disabled.", this);
		acquireTagTask.execute();

		sendTagEvent(tagEvent);
		
	}

	private void sendTagEvent(final TagEvent tagEvent){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SendTagEventAsyncTask task = new SendTagEventAsyncTask();
				task.execute(tagEvent);
			}
		}, 200);
		
	}
	
	private void enableForegroundDispatching() {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		IntentFilter[] intentFiltersArray = new IntentFilter[] { ndef };
		String[][] techFilters = new String[1][1];
		techFilters[0][0] = "android.nfc.tech.NfcA";
//		techFilters[0][0] = "android.nfc.tech.NfcA";
//		techFilters[0][1] = "android.nfc.tech.Ndef";
//		String[][] techFilters = new String[3][3];
//		techFilters[0][0] = "android.nfc.tech.NfcA";
//		techFilters[0][1] = "android.nfc.tech.Ndef";
//		techFilters[0][2] = "android.nfc.tech.MifareUltralight";
//		techFilters[1][0] = "android.nfc.tech.NfcA";
//		techFilters[1][1] = "android.nfc.tech.IsoDep";
//		techFilters[1][2] = "android.nfc.tech.NdefFormatable";
		NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techFilters);
	}

	private void disableForegroundDispatching() {
		NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
	}
	
	private void onTagAcquiryResponse(Tag tag) {
		if (tag == null) {
			NotificationUtils.showFeedbackMessage(ClaimTagActivity.this, "Failed to claim tag. Maybe it's already claimed by another user or it has been disabled.");
		} else {
			NotificationUtils.showFeedbackMessage(ClaimTagActivity.this, "Claimed tag " + tag.getIdentifier());
			onTagAcquired(tag);
		}
	}

	private class AcquireTagAsyncTask extends AcsApiAsyncTask<Void, Tag> {

		public AcquireTagAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public Tag doApiCall(Void... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			try {
				acquiring = true;
				TagApiClient tagApiClient = AcsApiClientFactory.createTagApiClient(settingsStore);
				return tagApiClient.acquire("me", "me", 6000);
			} finally {
				acquiring = false;
			}
		}

		@Override
		public void processOutput(Tag tag) {
			onTagAcquiryResponse(tag);
		}
	}
	
//	private class CancelAcquiryAsyncTask extends AcsApiAsyncTask<Void, Void> {
//
//		public CancelAcquiryAsyncTask(String acsApiExpMessage, Context ctx) {
//			super(acsApiExpMessage, ctx);
//		}
//
//		@Override
//		public Void doApiCall(Void... params) throws AcsApiException, NoInternetException {
//			try {
//				acquiring = true;
//				TouchatagRestClient client = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
//				client.cancelAcquiry(settingsStore.getClientName());
//				return null;
//			} finally {
//				acquiring = false;
//			}
//		}
//
//		@Override
//		public void processOutput(Void outpur) {
//			//onCancelledAcquiry();
//		}
//	}

	private class SendTagEventAsyncTask extends AsyncTask<TagEvent, Void, TagEventCommand> {

		@Override
		protected TagEventCommand doInBackground(TagEvent... params) {
			CorrelationGateway gateway = new CorrelationGateway(settingsStore.getUsername(), settingsStore.getPassword(), settingsStore.getServer());
			return gateway.handleTagEvent(params[0]);
		}

//		@Override
//		protected void onPostExecute(TagEventCommand result) {
//			super.onPostExecute(result);
//			TagEventFeedback feedback = result.getResponse();
//			if (feedback.getApplicationResponses().containsKey(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier())) {
//				LegacyClientActionResponse appResponse = (LegacyClientActionResponse) feedback.getApplicationResponses().get(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier());
//				Container con = appResponse.getClientAction().getContainer();
//				if (Container.TAG_MANAGEMENT.equalsIgnoreCase(con.getName())) {
//					String message = con.getContainer().getAttributes().get(Container.ATTR_MESSAGE);
//					if(TagEventFeedback.TAG_RECONFIGURATION.equals(message)){
//						onTagAcquirySuccessHint();
//					} else {
//						onUnexpectedTagResponse(message);
//					}
//				} 
//			}
//		}
		
	}
	
}
