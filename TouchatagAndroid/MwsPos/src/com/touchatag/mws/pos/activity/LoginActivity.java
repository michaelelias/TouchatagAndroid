package com.touchatag.mws.pos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.touchatag.android.correlation.api.v1_3.CorrelationGateway;
import com.touchatag.android.correlation.api.v1_3.command.PingCommand;
import com.touchatag.android.correlation.api.v1_3.model.ClientId;
import com.touchatag.android.correlation.api.v1_3.model.PingEvent;
import com.touchatag.mws.pos.R;
import com.touchatag.mws.pos.client.ServerInstance;
import com.touchatag.mws.pos.config.Settings;
import com.touchatag.mws.pos.util.NotificationUtils;

public class LoginActivity extends Activity {

	private EditText txtUsername;
	private EditText txtPassword;
	private Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		settings = new Settings(this);
		
		txtUsername = (EditText)findViewById(R.id.txt_login_username);
		txtPassword = (EditText)findViewById(R.id.txt_login_password);
		
		Button btnLogin = (Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(settings.getUsername() != null && settings.getPassword() != null){
			if(settings.getAccessToken() != null && settings.getAccessTokenSecret() != null){
				startActivity(new Intent(this, HomeActivity.class));
				finish();
			} else {
				startActivity(new Intent(this, AuthorizeActivity.class));
				finish();
			}
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	private void login() {
		if (validate()) {
			String username = txtUsername.getText().toString();
			String password = txtPassword.getText().toString();
			CorrelationGateway corrGateway = new CorrelationGateway(username, password, ServerInstance.BETA);
			PingEvent pingEvent = new PingEvent(new ClientId("12345", "clientname"));
			PingCommand command = corrGateway.ping(pingEvent);
			if(command.getResponseHttpStatusCode() == 200){
				onLoginSuccess();
			} else {
				onLoginFailed();
			}
		}
	}

	private void onLoginSuccess(){
		NotificationUtils.showFeedbackMessage(this, "Login successful.");
		String username = txtUsername.getText().toString();
		String password = txtPassword.getText().toString();
		settings.setUsername(username);
		settings.setPassword(password);
		Intent intent = new Intent(this, AuthorizeActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void onLoginFailed(){
		NotificationUtils.showFeedbackMessage(this, "Username of password incorrect.");
	}
	
	private boolean validate() {
		String username = txtUsername.getText().toString();
		if (username.length() == 0) {
			NotificationUtils.showFeedbackMessage(this, "Please enter your username.");
			return false;
		}
		String password = txtPassword.getText().toString();
		if (password.length() == 0) {
			NotificationUtils.showFeedbackMessage(this, "Please enter your password.");
			return false;
		}
		return true;
	}

}
