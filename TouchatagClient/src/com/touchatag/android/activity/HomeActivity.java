package com.touchatag.android.activity;

import oauth.signpost.exception.OAuthException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.touchatag.android.R;
import com.touchatag.android.client.rest.oauth.OAuthAuthorizer;
import com.touchatag.android.store.Server;
import com.touchatag.android.store.SettingsStore;

public class HomeActivity extends TabActivity {

	private static final int DIALOG_AUTHORIZE = 1;
	private static final int DIALOG_OAUTH_ERROR = 2;

	private SettingsStore settingsStore;

	private OAuthAuthorizer authorizer;

	private ProgressDialog progressDialog;

	private String oauthExceptionMessage;
	private TabHost tabHost;

	private void showFeedbackMessage(String feedbackMessage) {
		Toast toast = Toast.makeText(this.getApplicationContext(), feedbackMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);

		settingsStore = new SettingsStore(this);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		
		TabSpec appsTabSpec = tabHost.newTabSpec("tid1");
        TabSpec tagsTabSpec = tabHost.newTabSpec("tid1");
 
        Resources resources = getResources();
        appsTabSpec.setIndicator("Applications", resources.getDrawable(R.drawable.home_tab_apps));
        tagsTabSpec.setIndicator("Tags", resources.getDrawable(R.drawable.home_tab_tags));
 
        appsTabSpec.setContent(new Intent(this, AppsActivity.class));
        tagsTabSpec.setContent(new Intent(this, TagsActivity.class));
 
        tabHost.addTab(appsTabSpec);
        tabHost.addTab(tagsTabSpec);
 
        if (settingsStore.isAuthorized()) {
			tabHost.getTabWidget().setCurrentTab(0);
		} else {
			startAuthorization();
		}
	}

	
	private void startAuthorization(){
		Server server = settingsStore.getServer();
		if (server.getKey() != null && server.getSecret() != null) {

			authorizer = new OAuthAuthorizer(HomeActivity.this, server) {

				@Override
				public void onRequestTokenAuthorized(String token) {
					
				}

				@Override
				public void onAccessTokenReceived(String token, String tokenSecret) {
					settingsStore.setAccessToken(token);
					settingsStore.setAccessTokenSecret(tokenSecret);
					progressDialog.dismiss();
					showFeedbackMessage("Successfully authorized");
					Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
					HomeActivity.this.startActivity(intent);
					HomeActivity.this.finish();
				}

				@Override
				public void onRequireUserAuthorization() {
					progressDialog.dismiss();
					showDialog(DIALOG_AUTHORIZE);
				}

				@Override
				public void onRetrieveRequestToken() {
					// progressDialog.setMessage("Retrieving request token");
				}

				@Override
				public void onRetrieveAccessToken() {
					progressDialog.setMessage("Retrieving access token");
					progressDialog.show();
				}

				@Override
				public void onOAuthException(OAuthException e) {
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					oauthExceptionMessage = e.getMessage();
					showDialog(DIALOG_OAUTH_ERROR);
				}

			};

			RetrieveRequestTokenAsyncTask task = new RetrieveRequestTokenAsyncTask();
			task.execute(authorizer);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_AUTHORIZE:
			String message = "Touchatag Android App is trying to access your protected Touchtag resources. \n\nIf you want to allow this please click Authorize.";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Authorize access") //
					.setMessage(message) //
					.setCancelable(true) //
					.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							AuthorizeRequestTokenAsyncTask task = new AuthorizeRequestTokenAsyncTask();
							task.execute(authorizer);
						}
					}) //
					.setNegativeButton("Don't authorize", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			return builder.create();
		case DIALOG_OAUTH_ERROR:
			message = "A problem occurred while trying to perform an OAuth operation.\n\n" + oauthExceptionMessage;
			builder = new AlertDialog.Builder(this);
			builder.setTitle("OAuth Problem") //
					.setMessage(message) //
					.setCancelable(true) //
					.setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	class RetrieveRequestTokenAsyncTask extends AsyncTask<OAuthAuthorizer, Void, OAuthAuthorizer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(HomeActivity.this, "Authorization", "Retrieving request token", true);
		}

		@Override
		protected OAuthAuthorizer doInBackground(OAuthAuthorizer... params) {
			OAuthAuthorizer authorizer = params[0];
			authorizer.retrieveRequestToken();
			return authorizer;
		}

		@Override
		protected void onPostExecute(OAuthAuthorizer result) {
			super.onPostExecute(result);
		}
	}
	
	class AuthorizeRequestTokenAsyncTask extends AsyncTask<OAuthAuthorizer, Void, OAuthAuthorizer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(HomeActivity.this, "Authorization", "Authorizing request token", true);
		}
		
		@Override
		protected OAuthAuthorizer doInBackground(OAuthAuthorizer... params) {
			params[0].authorize();
			return params[0];
		}
		
	}
}
