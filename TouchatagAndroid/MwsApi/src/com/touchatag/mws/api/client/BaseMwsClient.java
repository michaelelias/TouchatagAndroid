package com.touchatag.mws.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.net.Uri;


public abstract class BaseMwsClient {

	public static int HTTP_TIMEOUT = 6000;
	public static String CALLBACK_URL = "touchatag://callback";
	private static String ENCODING = "utf-8";
	
	protected String baseURI;
	protected HttpClient httpClient;
	protected OAuthConsumer consumer;

	protected BaseMwsClient(MwsServer server, String accessToken, String accessTokenSecret) {
		Uri uri = Uri.parse(server.getUrl());
		int port = uri.getPort();

		baseURI = uri.getScheme() + "://" + uri.getHost() + (port != -1 ? ":" + port : "") + "/tikitag-web/rest/api";
		consumer = new CommonsHttpOAuthConsumer(server.getKey(), server.getSecret());
		consumer.setTokenWithSecret(accessToken, accessTokenSecret);

		if ("https".equals(uri.getScheme())) {
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			httpClient = new DefaultHttpClient();

			SchemeRegistry registry = new SchemeRegistry();
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, 443));
			SingleClientConnManager mgr = new SingleClientConnManager(httpClient.getParams(), registry);
			httpClient = new DefaultHttpClient(mgr, httpClient.getParams());
			// Set verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			httpClient.getParams().setParameter("https.socket.timeout", new Integer(HTTP_TIMEOUT));
		} else {
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter("http.socket.timeout", new Integer(HTTP_TIMEOUT));
		}
	}

	public static CommonsHttpOAuthProvider getOAuthProvider(String serverURI) {
		String baseOAuthURI = serverURI + "/tikitag-web/rest/oauth/";
		CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(baseOAuthURI + "request-token", baseOAuthURI + "access-token", baseOAuthURI + "authorize");
		// DefaultHttpClient httpClient = new DefaultHttpClient();
		// ProxySelectorRoutePlanner routePlanner = new
		// ProxySelectorRoutePlanner(httpClient.getConnectionManager().getSchemeRegistry(),
		// ProxySelector.getDefault());
		// httpClient.setRoutePlanner(routePlanner);
		// provider.setHttpClient(httpClient);
		return provider;
	}

	public boolean ping() throws MwsApiException, NoInternetException, UnexpectedHttpResponseCode {
		HttpGet httpGet = new HttpGet(baseURI + "/ping");
		HttpResponse response = connect(httpGet);
		processResponse(response, 200);
		return true;
	}

	protected boolean processResponse(HttpResponse response, int expectedHttpCode) throws MwsApiException, NoInternetException, UnexpectedHttpResponseCode {
		int statusCode = getStatusCode(response);
		if (statusCode == expectedHttpCode) {
			return true;
		} else if (statusCode == 500) {
			throw new MwsApiException();
		} else if (statusCode == 0) {
			throw new NoInternetException();
		} else {
			throw new UnexpectedHttpResponseCode();
		}
	}

	protected HttpResponse connect(HttpUriRequest req) {
		try {
			consumer.sign(req);
			return httpClient.execute(req);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	protected String getResonseBody(HttpResponse response) {
		try {
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(response.getEntity());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bufferedHttpEntity.writeTo(baos);
			return baos.toString("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
