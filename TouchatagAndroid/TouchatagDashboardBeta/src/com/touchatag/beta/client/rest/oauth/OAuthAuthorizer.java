package com.touchatag.beta.client.rest.oauth;

import java.net.ProxySelector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.touchatag.beta.store.Server;
import com.touchatag.beta.store.SettingsStore;

/**
 * Provides a facade for doing a web based oauth authorization flow. It wraps a
 * webview that is automated with javascript to perform an oauth authorization
 * action for the given consumer credentials.
 * 
 * The object makes assumptions about the HTML structure of the authorize web page.
 * 
 * @author Michael Elias
 * 
 */
public abstract class OAuthAuthorizer {

	private static final String TAG = OAuthAuthorizer.class.getSimpleName();

	private static final int HTTP_TIMEOUT = 6000;

	private enum State {
		INITIALIZED, RETRIEVING_REQUEST_TOKEN, AWAITING_USER_AUTHORIZATION, AUTHORIZING_REQUEST_TOKEN, RETRIEVED_ACCESS_TOKEN, OAUTH_EXCEPTION_OCCURRED;
	}

	private CommonsHttpOAuthConsumer consumer;
	private CommonsHttpOAuthProvider provider;
	private WebView webView;
	private SettingsStore settingsStore;
	private String authorizeButtonId;
	private State state;
	private Context ctx;
	private ProgressDialog progressDialog;

	public OAuthAuthorizer(Context ctx, Server server) {
		this.consumer = getConsumer(server.getKey(), server.getSecret());
		this.provider = getProvider(server.getUrl());
		settingsStore = new SettingsStore(ctx);
		this.ctx = ctx;

		state = State.INITIALIZED;
	}

	public void retrieveRequestToken() {
		try {
			onRetrieveRequestToken();
			//progressDialog = ProgressDialog.show(ctx, null, "Retrieving request token");
			state = State.RETRIEVING_REQUEST_TOKEN;
			String authorizeUrl = provider.retrieveRequestToken(consumer, "http://callback");
			Log.i(TAG, "request token : " + consumer.getToken());
			Log.i(TAG, "request token secret : " + consumer.getTokenSecret());
			Log.i(TAG, "authorize url : " + authorizeUrl);
			//progressDialog
			showAuthorizeDialog(authorizeUrl);
			//webView.loadUrl(authorizeUrl);
		} catch (OAuthException e) {
			state = State.OAUTH_EXCEPTION_OCCURRED;
			onOAuthException(e);
		}
	}

	/**
	 * Authorizes the request token.
	 * 
	 * @return
	 */
	public void authorize() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:(function(){");
		sb.append("document.getElementsByName('" + authorizeButtonId + "')[0].click();");
		sb.append("})()");
		state = State.AUTHORIZING_REQUEST_TOKEN;
		webView.loadUrl(sb.toString());
	}

	private CommonsHttpOAuthConsumer getConsumer(String key, String secret){
		return new CommonsHttpOAuthConsumer(key, secret);
	}
	
	private CommonsHttpOAuthProvider getProvider(String serverUrl){
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

	private void showAuthorizeDialog(String authorizeUrl){
//		Dialog dialog = new Dialog(ctx);
//		dialog.setContentView(R.layout.authorize_webview);
//		dialog.setTitle("Authorize");
//		
//		webView = (WebView)dialog.findViewById(R.id.webView);
//		
//		dialog.show();
	}
	
	public abstract void onOAuthException(OAuthException e);

	/**
	 * Called before a requesting a request token
	 */
	public abstract void onRetrieveRequestToken();

	/**
	 * Called when the user authorized the request token.
	 * 
	 * @param accessToken
	 */
	public abstract void onRequestTokenAuthorized(String token);

	/**
	 * Called before am authorized request token is exchanged for an access
	 * token
	 */
	public abstract void onRetrieveAccessToken();

	/**
	 * Called when an access token has been received
	 * 
	 * @param token
	 * @param tokenReceived
	 */
	public abstract void onAccessTokenReceived(String token, String tokenReceived);

	/**
	 * Called when the user is required to authorize the access to its protected
	 * resources.
	 */
	public abstract void onRequireUserAuthorization();
}
