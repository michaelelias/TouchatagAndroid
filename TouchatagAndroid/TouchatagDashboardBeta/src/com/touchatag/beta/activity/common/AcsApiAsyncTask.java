package com.touchatag.beta.activity.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.beta.util.NotificationUtils;

public abstract class AcsApiAsyncTask<INPUT, OUTPUT> extends AsyncTask<INPUT, Void, OUTPUT> {

	private String message;
	private String acsApiExpMessage;
	private Context ctx;
	private ProgressDialog progressDialog;
	private AcsApiException acsApiException;
	private NoInternetException noInternetException;
	private UnexpectedHttpResponseCodeException unexpectedHttpResponseCode;
	
	public AcsApiAsyncTask(String message, String acsApiExpMessage, Context ctx){
		this.message = message;
		this.acsApiExpMessage = acsApiExpMessage;
		this.ctx = ctx;
	}
	
	public AcsApiAsyncTask(String acsApiExpMessage, Context ctx){
		this.acsApiExpMessage = acsApiExpMessage;
		this.ctx = ctx;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(message != null){
			progressDialog = ProgressDialog.show(ctx, null, message, true, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					AcsApiAsyncTask.this.cancel(true);
					dialog.dismiss();
					NotificationUtils.showFeedbackMessage(ctx, "Cancelled API call.");
				}
			});
		}
	}
	
	@Override
	protected OUTPUT doInBackground(INPUT... params) {
		try {
			return doApiCall(params);
		} catch (AcsApiException e) {
			acsApiException = e;
		} catch (NoInternetException e) {
			noInternetException = e;
		} catch (UnexpectedHttpResponseCodeException e) {
			unexpectedHttpResponseCode = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(OUTPUT result) {
		super.onPostExecute(result);
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		if(result == null){
			if(acsApiException != null){
				NotificationUtils.showFeedbackMessage(ctx, acsApiExpMessage);
			}
			else if(noInternetException != null){
				NotificationUtils.showFeedbackMessage(ctx, "No internet connection");
			} else {
				NotificationUtils.showFeedbackMessage(ctx, "Unexpected response, please try again.");
			}
		} else {
			processOutput(result);
		}
	}

	public abstract OUTPUT doApiCall(INPUT... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException;

	public abstract void processOutput(OUTPUT output);

	
	
}
