package com.touchatag.beta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.touchatag.beta.R;
import com.touchatag.beta.client.UserManagementGateway;
import com.touchatag.beta.client.soap.model.common.Role;
import com.touchatag.beta.client.soap.model.common.UserDTO;
import com.touchatag.beta.client.soap.model.request.CreateUser;
import com.touchatag.beta.client.soap.model.request.GetUser;
import com.touchatag.beta.client.soap.model.response.GetUserResponse;
import com.touchatag.beta.store.Server;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.util.NotificationUtils;
import com.touchatag.beta.util.Utils;
import com.touchatag.beta.util.ValidationUtils;

public class RegisterActivity extends Activity {

	private SettingsStore settingsStore;
	private TextView txtUsername;
	private TextView txtPassword;
	private TextView txtEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		settingsStore = new SettingsStore(this);

		txtUsername = (TextView) findViewById(R.id.txt_register_username);
		txtPassword = (TextView) findViewById(R.id.txt_register_password);
		txtEmail = (TextView) findViewById(R.id.txt_register_email);

		Button btnCreateAccount = (Button) findViewById(R.id.btn_register_create_account);
		btnCreateAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createUser();
			}
		});
	}
	
	private void createUser(){
		if(validateForm()){
			UserDTO user = new UserDTO();
			user.setUsername(txtUsername.getText().toString());
			user.setPasswordCleartext(txtPassword.getText().toString());
			user.setPassword(Utils.md5(txtPassword.getText().toString()));
			user.setEmail(txtEmail.getText().toString());
			user.getRoles().add(Role.USER);
			user.getRoles().add(Role.BLOCK_EXTENSION);
			user.getRoles().add(Role.USER_MANAGER);
			new CreateUserAsyncTask().execute(user);
		}
	}
	
	private boolean validateForm(){
		if(txtUsername.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(this, "Please provide a username.");
			return false;
		}
		if(txtUsername.getText().length() < 4){
			NotificationUtils.showFeedbackMessage(this, "Username must be atleast 4 character long.");
			return false;
		}
		if(txtPassword.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(this, "Please provide a password.");
			return false;
		}
		if(txtPassword.getText().length() < 4){
			NotificationUtils.showFeedbackMessage(this, "Password must be atleast 4 character long.");
			return false;
		}
		if(txtEmail.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(this, "Please provide an email address.");
			return false;
		}
		if(!ValidationUtils.isValidEmail(txtEmail.getText().toString())){
			NotificationUtils.showFeedbackMessage(this, "Not a valid email address.");
			return false;
		}
		
		return true;
	}
	
	private void onCreatedAccount(UserDTO user){
		Intent intent = CredentialsActivity.getAutoLoginIntent(user.getUsername(), user.getPasswordCleartext(), this);
		startActivity(intent);
		finish();
	}

	private class CreateUserAsyncTask extends AsyncTask<UserDTO, Void, Boolean> {

		ProgressDialog progressDialog;
		private UserDTO user;
		private String errorMessage;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(RegisterActivity.this, null, "Creating Account...", true, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					CreateUserAsyncTask.this.cancel(true);
					dialog.dismiss();
					NotificationUtils.showFeedbackMessage(RegisterActivity.this, "Cancelled account creation.");
				}
			});
		}

		@Override
		protected Boolean doInBackground(UserDTO... params) {
			user = params[0];
			Server server = settingsStore.getServer();
			UserManagementGateway gateway = new UserManagementGateway(server.getProvisionerUsername(), server.getProvisionerPassword(), server);
			
			GetUser getUser = new GetUser();
			getUser.setUsername(user.getUsername());
			GetUserResponse getUserResponse = gateway.getUser(getUser);
			if(getUserResponse.getUser().getUsername() != null){
				errorMessage = "Username is already in use, provide another one.";
				return false;
			}
			
			CreateUser createUser = new CreateUser();
			createUser.setUser(user);
			return gateway.createUser(createUser);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if(result){
				NotificationUtils.showFeedbackMessage(RegisterActivity.this, "Successfully created account.");
				onCreatedAccount(user);
			} else {
				NotificationUtils.showFeedbackMessage(RegisterActivity.this, errorMessage != null ? errorMessage : "Failed to create account.");
			}
		}

	}

}
