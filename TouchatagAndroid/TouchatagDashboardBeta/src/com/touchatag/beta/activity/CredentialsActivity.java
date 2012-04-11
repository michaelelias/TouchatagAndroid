package com.touchatag.beta.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.touchatag.android.correlation.api.v1_2.command.BaseCommand.Logger;
import com.touchatag.android.correlation.api.v1_2.command.InvalidCredentialsException;
import com.touchatag.android.correlation.api.v1_2.command.PingCommand;
import com.touchatag.android.correlation.api.v1_2.command.SoapFaultException;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.PingEvent;
import com.touchatag.beta.R;
import com.touchatag.beta.TouchatagApplication;
import com.touchatag.beta.store.SettingsStore;

public class CredentialsActivity extends Activity {

	private static final int DIALOG_REGISTER = 1;
	private static final String EXTRA_USERNAME = "login.username";
	private static final String EXTRA_PASSWORD = "login.password";
	
	private ProgressDialog loginProgressDialog;
	private SettingsStore settingsStore;
	private EditText txtUsername;
	private EditText txtPassword;
	private Button btnLogin;

	public static Intent getAutoLoginIntent(String username, String password, Context ctx){
		Intent intent = new Intent(ctx, CredentialsActivity.class);
		intent.putExtra(EXTRA_USERNAME, username);
		intent.putExtra(EXTRA_PASSWORD, password);
		return intent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		settingsStore = new SettingsStore(this);

		if (settingsStore.hasCredentials()) {
			startHomeActivity();
			return;
		}

		setContentView(R.layout.credentials);

		txtUsername = (EditText)findViewById(R.id.txt_credentials_username);
		txtPassword = (EditText)findViewById(R.id.txt_credentials_password);
		
		btnLogin = (Button) findViewById(R.id.btn_credentials_login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLogin();
			}
		});
		
		Button btnRegister = (Button) findViewById(R.id.btn_credentials_register);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(CredentialsActivity.this, RegisterActivity.class));
				//showDialog(DIALOG_REGISTER);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if(intent.getStringExtra(EXTRA_USERNAME) != null && intent.getStringExtra(EXTRA_PASSWORD) != null){
			txtUsername.setText(intent.getStringExtra(EXTRA_USERNAME));
			txtPassword.setText(intent.getStringExtra(EXTRA_PASSWORD));
			doLogin();
		}
	}

	private void doLogin(){
		if (validateLoginForm()) {
			new CheckCredentialsAsyncTask().execute(getCredentials());
		}
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
//		CorrelationGateway ttGateway = new CorrelationGateway(username, password, settingsStore.getServer());
//
//		ClientId clientId = new ClientId();
//
//		clientId.setId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
//		clientId.setName(settingsStore.getClientName());
//
//		ReaderId readerId = new ReaderId("0123456".getBytes(), "123456789");
//
//		PingEvent pingEvent = new PingEvent();
//		pingEvent.setClientId(clientId);
//
//		return ttGateway.ping(pingEvent);
		com.touchatag.android.correlation.api.v1_2.CorrelationGateway gateway = new com.touchatag.android.correlation.api.v1_2.CorrelationGateway(username, password, settingsStore.getServer()) {
			
			@Override
			public Logger createLogger() {
				
				return new Logger() {

					@Override
					public void log(String message) {
						Log.i("Gateway", message);
					}
				};
			}
		};
		
		PingEvent pingEvent = new PingEvent(new ClientId(Secure.getString(getContentResolver(), Secure.ANDROID_ID), settingsStore.getClientName()));
		
		try {
			return gateway.ping(pingEvent);
		} catch (InvalidCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SoapFaultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
