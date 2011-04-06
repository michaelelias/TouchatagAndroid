package com.touchatag.android.client.rest.oauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProxySelector;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.signature.HmacSha1MessageSigner;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.touchatag.android.store.Server;
import com.touchatag.android.store.SettingsStore;

/**
 * Provides a facade for doing a web based oauth authorization flow. It wraps a
 * webview that is automated with javascript to perform an oauth authorization
 * action for the given consumer credentials.
 * 
 * The object makes assumptions about the structure of the authorize web page.
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

	public OAuthAuthorizer(Context ctx, Server server) {
		this.consumer = getConsumer(server.getKey(), server.getSecret());
		this.provider = getProvider(server.getUrl());
		settingsStore = new SettingsStore(ctx);
		webView = new WebView(ctx);

		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebChromeClient(new WebChromeClient(){

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
				//sb.append("alert('is login page : ' + isLoginPage);");
				//sb.append("alert(document.getElementsByTagName('html')[0].innerHTML);");
				sb.append("if(isLoginPage){");
				//sb.append(" alert('entered page');");
				sb.append("	document.getElementById('username').value = '" + settingsStore.getUsername() + "';");
				//sb.append(" alert(document.getElementById('username').value);");
				sb.append("	document.getElementById('password').value = '" + settingsStore.getPassword() + "';");
				//sb.append(" alert(document.getElementById('password').value);");
				//sb.append(" alert(document.getElementById('login'));");
				sb.append("	document.getElementById('login').click();");
				sb.append("	return;");
				sb.append("}");
				sb.append("var isAuthorizePage = document.forms['authorizeConsumerForm'] != null;");
				//sb.append("alert('is authorize page : ' + isAuthorizePage);");
				sb.append("if(isAuthorizePage){");
				sb.append("	window.location = 'http://authorize?buttonId=Authorize';");
				sb.append("}");
				
				sb.append("})()");
				webView.loadUrl(sb.toString());
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http://authorize")) {
					if (state == State.RETRIEVING_REQUEST_TOKEN) {
						Log.i(TAG, "Retrieving request token");
						Uri uri = Uri.parse(url);
						String[] queryParts = uri.getEncodedQuery().split("\\&");
						String[] queryPart = queryParts[0].split("=");
						authorizeButtonId = queryPart[1];
						state = State.AWAITING_USER_AUTHORIZATION;
						Log.i(TAG, "Awaiting user authorization");
						onRequireUserAuthorization();
					}
					return true;
				} else if (url.startsWith("http://callback")) {
					if (state == State.AUTHORIZING_REQUEST_TOKEN) {
						Log.i(TAG, "Authorizing request token");
						Uri uri = Uri.parse(url);
						String[] queryParts = uri.getEncodedQuery().split("\\&");
						String[] queryPart = queryParts[0].split("=");
						String requestToken = queryPart[1];
						onRequestTokenAuthorized(requestToken);
						try {
							onRetrieveAccessToken();
							OAuthAuthorizer.this.provider.retrieveAccessToken(OAuthAuthorizer.this.consumer, requestToken);
							Log.i("OAuthAuthorizer", "Access Token : " + OAuthAuthorizer.this.consumer.getToken());
							Log.i("OAuthAuthorizer", "Access Token Secret : " + OAuthAuthorizer.this.consumer.getTokenSecret());
							onAccessTokenReceived(OAuthAuthorizer.this.consumer.getToken(), OAuthAuthorizer.this.consumer.getTokenSecret());
							state = State.RETRIEVED_ACCESS_TOKEN;
						} catch (OAuthException e) {
							state = State.OAUTH_EXCEPTION_OCCURRED;
							onOAuthException(e);
						}
					}
					return true;
				}
				return false;
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				Log.i(TAG, "Received an SSL error : " + error.toString());
			}
			
			

		});

		state = State.INITIALIZED;
	}

	public void retrieveRequestToken() {
		try {
			onRetrieveRequestToken();
			state = State.RETRIEVING_REQUEST_TOKEN;
			String authorizeUrl = provider.retrieveRequestToken(consumer, "http://callback");
			Log.i(TAG, "request token : " + consumer.getToken());
			Log.i(TAG, "request token secret : " + consumer.getTokenSecret());
			Log.i(TAG, "authorize url : " + authorizeUrl);
			webView.loadUrl(authorizeUrl);
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
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
		client.setRoutePlanner(routePlanner);

		String baseUrl = serverUrl + "/tikitag-web/rest/oauth";
		return new CommonsHttpOAuthProvider(baseUrl + "/request-token", baseUrl + "/access-token", baseUrl + "/authorize", client);
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
