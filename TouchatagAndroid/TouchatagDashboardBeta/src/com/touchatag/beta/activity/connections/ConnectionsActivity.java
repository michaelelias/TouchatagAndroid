package com.touchatag.beta.activity.connections;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.MetadataApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.util.NotificationUtils;

public class ConnectionsActivity extends Activity {

	private SettingsStore settingsStore;
	private Button btnFoursquareConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connections);

		settingsStore = new SettingsStore(this);

		btnFoursquareConnect = (Button) findViewById(R.id.connections_btn_connect_foursquare);
		btnFoursquareConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (settingsStore.hasAccessTokenFoursquare()) {
					revokeAccess(Connection.FOURSQUARE);
				} else {
					connectToFoursquare();
				}
			}
		});

		if (settingsStore.hasAccessTokenFoursquare()) {
			btnFoursquareConnect.setText("Revoke Access");
		}

		Button btnGowallaConnect = (Button) findViewById(R.id.connections_btn_connect_gowalla);
		btnGowallaConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectToGowalla();
			}
		});
	}

	private void revokeAccess(Connection connection) {
		ClearConnectionAccessTokenAsyncTask task = new ClearConnectionAccessTokenAsyncTask(this, connection);
		task.execute(settingsStore.getAccessTokenFoursquare());
	}

	private void connectToFoursquare() {
		startActivity(AuthenticateConnectionActivity.getAuthenticateConnectionIntent(this, Connection.FOURSQUARE));
	}

	private void connectToGowalla() {
		startActivity(AuthenticateConnectionActivity.getAuthenticateConnectionIntent(this, Connection.GOWALLA));
	}

	private void clearConnectionAccessTokenMetadataItem(Connection connection) {
		switch(connection){
		case FOURSQUARE :
			settingsStore.clearAccessTokenFoursquare();
			btnFoursquareConnect.setText("Connect");
			break;
		}
		String connectionName = connection.name().toLowerCase();
		NotificationUtils.showFeedbackMessage(this, "Revoked access for " + connectionName);
	}
	
	private class ClearConnectionAccessTokenAsyncTask extends AcsApiAsyncTask<String, Boolean> {

		private Connection connection;

		public ClearConnectionAccessTokenAsyncTask(Context ctx, Connection connection) {
			super("Revoking authorization", "Failed to revoke authorization", ctx);
			this.connection = connection;
		}

		@Override
		public Boolean doApiCall(String... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			String itemId = params[0];
			MetadataApiClient metadataApi = AcsApiClientFactory.createMetadataApiClient(settingsStore);

			return metadataApi.deleteItem(itemId);
		}

		@Override
		public void processOutput(Boolean output) {
			if (output) {
				clearConnectionAccessTokenMetadataItem(connection);
			}
		}

	}
}
