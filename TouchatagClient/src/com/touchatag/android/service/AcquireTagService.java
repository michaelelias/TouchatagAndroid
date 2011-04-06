package com.touchatag.android.service;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;

import com.touchatag.android.client.TouchatagRestClient;
import com.touchatag.android.client.rest.model.AcsApiException;
import com.touchatag.android.client.rest.model.Error;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.store.SettingsStore;

public class AcquireTagService extends IntentService {

	public static final Uri URI_ACQUIRETAG = Uri.parse("touchatag://acquiretag");
	public static final String ACTION_START_ACQUIRE_TAG = "com.touchatag.action.START_ACQUIRE_TAG";
	public static final String ACTION_CANCEL_ACQUIRE_TAG = "com.touchatag.action.CANCEL_ACQUIRE_TAG";
	public static final String EXTRA_TIMEOUT = "timeout";
	public static final String EXTRA_CLIENTNAME = "clientname";
	public static final String EXTRA_RESULT_LISTENER = "tagacquiryresultlistener";
	
	private static TagAcquiryResultListener listener;
	
	public static final Intent getStartAcquiryIntent(String clientName, int timeout, TagAcquiryResultListener listener){
		Intent intent = new Intent(ACTION_START_ACQUIRE_TAG);
		intent.putExtra(EXTRA_CLIENTNAME, clientName);
		intent.putExtra(EXTRA_TIMEOUT, timeout);
		AcquireTagService.listener = listener;
		return intent;
	}
	
	public static final Intent getCancelAcquiryIntent(String clientName, int timeout){
		Intent intent = new Intent(ACTION_CANCEL_ACQUIRE_TAG);
		intent.putExtra(EXTRA_CLIENTNAME, clientName);
		intent.putExtra(EXTRA_TIMEOUT, timeout);
		return intent;
	}
	
	public AcquireTagService() {
		super("touchatag.service.acquiretag");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SettingsStore store = new SettingsStore(this);
		TouchatagRestClient client = TouchatagRestClient.create(store.getServer(), store.getAccessToken(), store.getAccessTokenSecret());
		String clientName = intent.getExtras().getString(EXTRA_CLIENTNAME);
		int timeout = intent.getExtras().getInt(EXTRA_TIMEOUT);
		if(ACTION_START_ACQUIRE_TAG.equals(intent.getAction())){
			try {
				listener.onAcquirySuccess(client.acquireTag(clientName, timeout));
			} catch (AcsApiException e) {
				listener.onAcquiryFailure(e.getError());
			}
		} else if(ACTION_CANCEL_ACQUIRE_TAG.equals(intent.getAction())){
			
		}
	}
	
	public interface TagAcquiryResultListener {
		
		public void onAcquirySuccess(Tag tag);
		
		public void onAcquiryFailure(Error error);
		
	}

}
