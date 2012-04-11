package com.touchatag.beta.activity.connections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProxySelector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.MetadataApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.MetadataAssociation;
import com.touchatag.acs.api.client.model.MetadataHolder;
import com.touchatag.acs.api.client.model.MetadataHolderType;
import com.touchatag.acs.api.client.model.MetadataItem;
import com.touchatag.acs.api.client.model.MetadataItemPage;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.SettingsStore;

public class AuthenticateConnectionActivity extends Activity implements ConnectionAuthorizer {

	public static final String EXTRA_CONNECTION = "connection";

	private WebView webView;
	private DefaultHttpClient httpClient;

	private SettingsStore settingsStore;

	private Connection connection;

	public static Intent getAuthenticateConnectionIntent(Context ctx, Connection connection) {
		Intent intent = new Intent(ctx, AuthenticateConnectionActivity.class);
		intent.putExtra(EXTRA_CONNECTION, connection.name());
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Authorization");
		setContentView(R.layout.authorize);

		String connectionName = getIntent().getStringExtra(EXTRA_CONNECTION);
		connection = Connection.valueOf(connectionName);
		settingsStore = new SettingsStore(this);
		createHttpClient();

		webView = (WebView) findViewById(R.id.authorize_webview);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.getSettings().setSaveFormData(false);
		// webView.getSettings().setSavePassword(false);
		// webView.getSettings().setDatabaseEnabled(false);
		// webView.getSettings().setAppCacheEnabled(false);
		// webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// CookieSyncManager.createInstance(this);
		// CookieManager.getInstance().removeSessionCookie();

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				return false;
			}

		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return connection.onAuthorized(url, AuthenticateConnectionActivity.this);

			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

		});

		connection.onAuthorize(this);
	}

	@Override
	public void authorize(String authorizeUrl) {
		webView.loadUrl(authorizeUrl);
	}

	@Override
	public void retrieveToken(HttpUriRequest httpUriRequest) {
		try {
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpResponse.getEntity());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bufferedHttpEntity.writeTo(baos);
			connection.onTokenRetrieved("", baos.toString("UTF-8"), this);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void storeAccessToken(Connection connection, String accessToken) {
		StoreConnectionAccessTokenAsyncTask task = new StoreConnectionAccessTokenAsyncTask(this, connection);
		task.execute(accessToken);
	}

	@Override
	public SettingsStore getSettingsStore() {
		return settingsStore;
	}

	private void createHttpClient() {
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		httpClient = new DefaultHttpClient();

		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(httpClient.getParams(), registry);
		DefaultHttpClient client = new DefaultHttpClient(mgr, httpClient.getParams());

		// Set verifier
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(httpClient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
		httpClient.setRoutePlanner(routePlanner);
	}

	private void storeConnectionAccessTokenMetadataItem(Connection connection, String metadataItemId) {
		switch(connection){
		case FOURSQUARE :
			settingsStore.setAccessTokenFoursquare(metadataItemId);
			break;
		}
	}
	
	private void clearConnectionAccessTokenMetadataItem(Connection connection) {
		switch(connection){
		case FOURSQUARE :
			settingsStore.clearAccessTokenFoursquare();
			break;
		}
	}

	private class StoreConnectionAccessTokenAsyncTask extends AcsApiAsyncTask<String, MetadataItem> {

		private Connection connection;
		
		public StoreConnectionAccessTokenAsyncTask(Context ctx, Connection connection) {
			super("Storing authorization", "Failed to store authorization", ctx);
			this.connection = connection;
		}

		@Override
		public MetadataItem doApiCall(String... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			String accessToken = params[0];
			String metadataType = connection.getMetadataType();
			MetadataApiClient metadataApi = AcsApiClientFactory.createMetadataApiClient(settingsStore);

			MetadataItemPage page = metadataApi.getItemPage(1, 25);
			for (MetadataItem item : page.getItems()) {
				if (item.getType().equals(metadataType)) {
					// update item
					item.setValue(accessToken);
					item = metadataApi.updateItem(item);
					return item;
				}
			}
			MetadataItem item = new MetadataItem();
			item.setOwnerId(settingsStore.getIdentityId());
			item.setType(metadataType);
			item.setValue(accessToken);

			item = metadataApi.createItem(item);
			
			MetadataAssociation asso = new MetadataAssociation();
			MetadataHolder holder = new MetadataHolder();
			holder.setReference(settingsStore.getIdentityId());
			holder.setType(MetadataHolderType.USER);
			asso.setMetadataHolder(holder);
			asso.setMetadataItemId(item.getId());
			asso = metadataApi.createAssociation(asso);
			
			return item;
		}

		@Override
		public void processOutput(MetadataItem output) {
			storeConnectionAccessTokenMetadataItem(connection, output.getId());
		}

	}
	
	

}
