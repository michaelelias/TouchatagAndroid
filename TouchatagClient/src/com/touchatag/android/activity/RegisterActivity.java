package com.touchatag.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.client.UserManagementGateway;
import com.touchatag.android.client.soap.model.common.Role;
import com.touchatag.android.client.soap.model.common.UserDTO;
import com.touchatag.android.client.soap.model.request.CreateUser;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.util.NotificationUtils;

public class RegisterActivity extends Activity {

	private SettingsStore settingsStore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		settingsStore = new SettingsStore(this);

		final TextView txtUsername = (TextView) findViewById(R.id.txt_register_username);
		final TextView txtPassword = (TextView) findViewById(R.id.txt_register_password);
		final TextView txtEmail = (TextView) findViewById(R.id.txt_register_email);

		Button btnCreateAccount = (Button) findViewById(R.id.btn_register_create_account);
		btnCreateAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserDTO user = new UserDTO();
				user.setUsername(txtUsername.getText().toString());
				user.setPassword(txtPassword.getText().toString());
				user.setEmail(txtEmail.getText().toString());
				user.getRoles().add(Role.USER);
				new CreateUserAsyncTask().execute(user);
			}
		});
	}

	private class CreateUserAsyncTask extends AsyncTask<UserDTO, Void, Boolean> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(RegisterActivity.this, null, "Creating Account...");
		}

		@Override
		protected Boolean doInBackground(UserDTO... params) {
			UserManagementGateway gateway = new UserManagementGateway("michaele", "dropdead_88", settingsStore.getServerEndpoint());
			CreateUser createUser = new CreateUser();
			createUser.setUser(params[0]);
			return gateway.createUser(createUser);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if(result){
				NotificationUtils.showFeedbackMessage(RegisterActivity.this, "Successfully created account.");
			} else {
				NotificationUtils.showFeedbackMessage(RegisterActivity.this, "Failed to create account.");
			}
		}

	}

}
