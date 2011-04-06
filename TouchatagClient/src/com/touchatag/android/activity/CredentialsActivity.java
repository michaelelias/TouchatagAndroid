package com.touchatag.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.touchatag.android.R;
import com.touchatag.android.TouchatagApplication;
import com.touchatag.android.client.CorrelationGateway;
import com.touchatag.android.client.soap.command.PingCommand;
import com.touchatag.android.client.soap.model.common.ClientId;
import com.touchatag.android.client.soap.model.common.ReaderId;
import com.touchatag.android.client.soap.model.request.PingEvent;
import com.touchatag.android.store.SettingsStore;

public class CredentialsActivity extends BaseActivity {

	private static final int DIALOG_REGISTER = 1;
	
	private ProgressDialog loginProgressDialog;
	private SettingsStore settingsStore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settingsStore = new SettingsStore(this);

		if (settingsStore.hasCredentials()) {
			startHomeActivity();
			return;
		}

		setContentView(R.layout.credentials);

		Button button = (Button) findViewById(R.id.btn_credentials_login);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (validateLoginForm()) {
					new CheckCredentialsAsyncTask().execute(getCredentials());
				}
			}
		});
		
		button = (Button) findViewById(R.id.btn_credentials_register);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(CredentialsActivity.this, RegisterActivity.class));
				//showDialog(DIALOG_REGISTER);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DIALOG_REGISTER){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Register with Touchatag") //
				   .setIcon(android.R.drawable.ic_dialog_info)
				   .setMessage("You can create an account on the Touchatag website. Do you want to be redirected to the registration page?") //
			       .setCancelable(true) //
			       .setPositiveButton("Yes please", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.touchatag.com/user/register"));
			                startActivity(intent);
			           }
			       }) //
			       .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			return builder.create();
		} 
		return super.onCreateDialog(id);
	}

	private void startHomeActivity() {
		if(TouchatagApplication.FULL){
			startActivity(new Intent(CredentialsActivity.this, HomeActivity.class));
		} else {
			startActivity(new Intent(CredentialsActivity.this, LiteHomeActivity.class));
		}
		finish();
	}

	private boolean validateLoginForm() {
		String username = getUsername();
		String password = getPassword();

		boolean validated = true;
		String feedbackMessage = "";

		if (username.length() == 0) {
			feedbackMessage = "Please enter your username.";
			validated = false;
		} else if (password.length() == 0) {
			feedbackMessage = "Please enter your password.";
			validated = false;
		} 

		if (!validated) {
			showFeedbackMessage(feedbackMessage);
		}

		return validated;
	}

	private Credentials getCredentials() {
		return new Credentials(getUsername(), getPassword());
	}

	private String getUsername() {
		EditText txtUsername = (EditText) findViewById(R.id.txt_credentials_username);
		return txtUsername.getText().toString();
	}

	private String getPassword() {
		EditText txtPassword = (EditText) findViewById(R.id.txt_credentials_password);
		return txtPassword.getText().toString();
	}

	private PingCommand doLogin(String username, String password) {
		CorrelationGateway ttGateway = new CorrelationGateway(username, password, settingsStore.getServerEndpoint());

		ClientId clientId = new ClientId();

		clientId.setId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		clientId.setName(settingsStore.getClientName());

		ReaderId readerId = new ReaderId("0123456".getBytes(), "123456789");

		PingEvent pingEvent = new PingEvent();
		pingEvent.setClientId(clientId);

		return ttGateway.ping(pingEvent);
	}

	private void showFeedbackMessage(String feedbackMessage) {
		Toast toast = Toast.makeText(this.getApplicationContext(), feedbackMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

	private void showLoginProgressDialog() {
		if (loginProgressDialog == null) {
			loginProgressDialog = ProgressDialog.show(this, "", "Logging into Touchatag server", true);
		} else {
			loginProgressDialog.show();
		}
	}

	private void hideLoginProgressDialog() {
		if (loginProgressDialog != null && loginProgressDialog.isShowing()) {
			loginProgressDialog.hide();
		}
	}

	private class CheckCredentialsAsyncTask extends AsyncTask<Credentials, Void, PingCommand> {

		@Override
		protected void onPreExecute() {
			showLoginProgressDialog();
		}

		@Override
		protected PingCommand doInBackground(Credentials... params) {
			return doLogin(getUsername(), getPassword());
		}

		@Override
		protected void onPostExecute(PingCommand pingCommand) {
			hideLoginProgressDialog();

			switch (pingCommand.getResponseHttpStatusCode()) {
			case 0 :
				showFeedbackMessage("Connection timeout, check your Internet connection");
				break;
			case 401:
				showFeedbackMessage("Sorry, invalid username or password.");
				break;
			case 200:
				showFeedbackMessage("Login successful");
				SettingsStore settingsStore = new SettingsStore(CredentialsActivity.this);
				settingsStore.storeUsername(getUsername());
				settingsStore.storePassword(getPassword());
				startHomeActivity();
				break;
			default:
				showFeedbackMessage("Received HTTP response code " + pingCommand.getResponseHttpStatusCode());
			}

		}

	}
}
