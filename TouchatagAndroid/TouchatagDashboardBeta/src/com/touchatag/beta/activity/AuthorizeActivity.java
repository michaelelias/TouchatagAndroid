package com.touchatag.beta.activity;

import java.net.ProxySelector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.AcsIdentityApiClient;
import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.AcsIdentity;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.Server;
import com.touchatag.beta.store.SettingsStore;

public class AuthorizeActivity extends Activity {

	private static String TAG = AuthorizeActivity.class.getSimpleName();

	private CommonsHttpOAuthConsumer consumer;
	private CommonsHttpOAuthProvider provider;

	private SettingsStore settingsStore;
	private AssociationStore assStore;

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Authorization");
		
		settingsStore = new SettingsStore(this);
		assStore = new AssociationStore(this);
		
		setContentView(R.layout.authorize);

		webView = (WebView) findViewById(R.id.authorize_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSaveFormData(false);
		webView.getSettings().setSavePassword(false);
		webView.getSettings().setDatabaseEnabled(false);
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().removeSessionCookie();

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Log.i(TAG, "jsalert : " + message);
				return false;
			}

		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "On page finished loading : " + url);
				StringBuilder sb = new StringBuilder();
				sb.append("javascript:(function(){");
				sb.append("var loginForm = document.forms[0];");
				sb.append("var isLoginPage = loginForm != null && loginForm.action == 'j_security_check';");
				sb.append("if(isLoginPage){");
				sb.append("	document.getElementById('username').value = '" + settingsStore.getUsername() + "';");
				sb.append("	document.getElementById('password').value = '" + settingsStore.getPassword() + "';");
				sb.append("}");
				sb.append("})()");
				webView.loadUrl(sb.toString());
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http://authorize")) {
					Log.i(TAG, "Retrieving request token");
				} else if (url.startsWith("http://callback")) {
					Log.i(TAG, "Authorizing request token");
					Uri uri = Uri.parse(url);
					String[] queryParts = uri.getEncodedQuery().split("\\&");
					String[] queryPart = queryParts[0].split("=");
					String requestToken = queryPart[1];
					try {
						provider.retrieveAccessToken(consumer, requestToken);
						onAccessTokenReceived(consumer.getToken(), consumer.getTokenSecret());
					} catch (OAuthException e) {
						onOAuthException(e);
					}
					return true;
				}
				return false;
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				Log.i(TAG, "Received an SSL error : " + error.toString());
				handler.proceed();
			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.clearCache(true);
		Server server = settingsStore.getServer();
		this.consumer = getConsumer(server.getKey(), server.getSecret());
		this.provider = getProvider(server.getUrl());
		new RetrieveRequestTokenAsyncTask().execute();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		settingsStore.revokeAuthorization();
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

	private void onRequestTokenReceived(String authorizeUrl) {
		webView.loadUrl(authorizeUrl);
	}

	private void onOAuthException(OAuthException e) {

	}

	private void onAccessTokenReceived(String key, String secret) {
		settingsStore.setAccessToken(key);
		settingsStore.setAccessTokenSecret(secret);
		loadCorrelationDefinition();
	}

	private CommonsHttpOAuthConsumer getConsumer(String key, String secret) {
		return new CommonsHttpOAuthConsumer(key, secret);
	}

	private CommonsHttpOAuthProvider getProvider(String serverUrl) {
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		DefaultHttpClient client = new DefaultHttpClient();

		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

		// Set verifier
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
		client.setRoutePlanner(routePlanner);

		String baseUrl = serverUrl + "/tikitag-web/rest/oauth";
		return new CommonsHttpOAuthProvider(baseUrl + "/request-token", baseUrl + "/access-token", baseUrl + "/authorize", client);
	}

	private void loadCorrelationDefinition() {
		new GetCorrelationDefinitionAsyncTask("Loading Account...", "Failed to load your account.", this).execute();
	}

	private void onCorrelationDefinitionLoaded(CorrelationDefinition corrDef) {
		assStore.update(corrDef);
		settingsStore.setIdentityId(corrDef.getOwnerId());
		new SetTagOwnerScopeAllowedAsyncTask("Updating Account...", "Failed to update your account.", this).execute(corrDef.getOwnerId());
	}
	
	private void onSetTagOwnerScopeCompleted(boolean success){
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private class RetrieveRequestTokenAsyncTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(AuthorizeActivity.this, null, "Initializing Authorization...");
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				return provider.retrieveRequestToken(consumer, "http://callback");
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String authorizeUrl) {
			super.onPostExecute(authorizeUrl);
			progressDialog.dismiss();
			onRequestTokenReceived(authorizeUrl);
		}

	}
	
	private class GetCorrelationDefinitionAsyncTask extends AcsApiAsyncTask<Void, CorrelationDefinition> {

		public GetCorrelationDefinitionAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public CorrelationDefinition doApiCall(Void... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			CorrelationDefinition corrDef = correlationDefinitionApi.get();
			return corrDef;
		}

		@Override
		public void processOutput(CorrelationDefinition corrDef) {
			onCorrelationDefinitionLoaded(corrDef);
		}
	}
	
	private class SetTagOwnerScopeAllowedAsyncTask extends AcsApiAsyncTask<String, Boolean> {

		public SetTagOwnerScopeAllowedAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public Boolean doApiCall(String... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			String identityId = params[0];
			AcsIdentityApiClient acsIdentityApi = AcsApiClientFactory.createAcsIdentityApiApiClient(settingsStore);
			AcsIdentity acsIdentity = acsIdentityApi.get(identityId);
			acsIdentity.setTagOwnerScopeAllowed(true);
			acsIdentityApi.update(acsIdentity);
			return true;
		}

		@Override
		public void processOutput(Boolean success) {
			onSetTagOwnerScopeCompleted(success);
		}
	}

}
