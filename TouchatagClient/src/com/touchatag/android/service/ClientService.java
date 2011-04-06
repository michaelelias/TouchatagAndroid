package com.touchatag.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.Settings.Secure;
import android.widget.Toast;

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
import com.touchatag.android.tag.mifare.ultralight.CouldNotReadTagDataException;
import com.touchatag.android.tag.mifare.ultralight.MifareUltralightTagHandler;
import com.touchatag.android.util.NdefMessageParser;

public class ClientService extends IntentService {

	public static final String ACTION_SEND_TAG_EVENT = "com.touchatag.action.SEND_TAG_EVENT";
	public static final String EXTRA_TAG_EVENT_TYPE = "tageventtype";
	public static final String EXTRA_CLIENTNAME = "clientname";

	private SettingsStore settingsStore;
	private CorrelationGateway gateway;

	public ClientService() {
		super("touchatag.service.client");
		settingsStore = new SettingsStore(this);
		gateway = new CorrelationGateway(settingsStore.getUsername(), settingsStore.getPassword(), settingsStore.getServer().getUrl());
	}
	
	//public static void startedAcquireTag(){

	public static Intent getSendTouchTagEventIntent() {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String action = intent.getAction();
		if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Uri uri = extractUriFromIntent(intent);
			
			MifareUltralightTagHandler tagHandler = new MifareUltralightTagHandler(tag);
			byte[] tagData = new byte[0];
			try {
				tagData = tagHandler.readAll();
			} catch (CouldNotReadTagDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String tagUID = new String(tagHandler.getTagUID());
			TagEventCommand command = gateway.handleTagEvent(createTouchTagEvent(tagUID, tagData));
			
			switch (command.getResponseHttpStatusCode()) {
			case 0:
				showFeedbackMessage("Connection timed out.");
				break;
			case 500:
				showFeedbackMessage("The server encountered an error, please try again.");
				break;
			case 401:
				showFeedbackMessage("Your Touchatag username or password is not correct");
				break;
			case 200:
				break;
			}

			processResponse(command.getResponse());
			
			tagHandler.release();
		}
	}

	private void processResponse(TagEventFeedback feedback){
		if (feedback == null) {
			showFeedbackMessage("Oops! something went wrong! Try again.");
		}
		if (feedback.getSystemMessage() != null) {
			showFeedbackMessage(feedback.getSystemMessage());
		} else if (feedback.getApplicationResponses().size() > 0) {
			if (feedback.getApplicationResponses().containsKey(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier())) {
				LegacyClientActionResponse appResponse = (LegacyClientActionResponse) feedback.getApplicationResponses().get(ApplicationResponseType.LEGACY_CLIENT_ACTION.getIdentifier());
				Container con = appResponse.getClientAction().getContainer();
				if (Container.TAG_MANAGEMENT.equalsIgnoreCase(con.getName())) {
					String tagReconfigMessage = con.getContainer().getAttributes().get(Container.ATTR_MESSAGE);
					showFeedbackMessage(tagReconfigMessage);
				} else if (Container.URL.equalsIgnoreCase(con.getName())) {
					String url = con.getContainer().getAttributes().get(Container.ATTR_URL);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}
			}
		}
	}
	
	private void showFeedbackMessage(String feedbackMessage) {
		Toast toast = Toast.makeText(this.getApplicationContext(), feedbackMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private TagEvent createTouchTagEvent(String tagUID, byte[] tagData) {
		ClientId clientId = new ClientId();
		String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		clientId.setId(androidId);
		clientId.setName(settingsStore.getClientName());

		ReaderId readerId = new ReaderId(androidId.getBytes(), androidId);

		TagId tagId = new TagId();
		tagId.setGenericTagType(GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT);
		tagId.setIdentifier("0x" + tagUID);

		TagInfo actionTag = new TagInfo(tagId, tagData);

		TagEvent tagEvent = new TagEvent();
		tagEvent.setClientId(clientId);
		tagEvent.setReaderId(readerId);
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setActionTag(actionTag);

		return tagEvent;
	}

	private Parcelable extractTagFromIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			return intent.getParcelableExtra("android.nfc.extra.TAG");
		}
		return null;
	}

	private Uri extractUriFromIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
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
		}
		return null;
	}

}
