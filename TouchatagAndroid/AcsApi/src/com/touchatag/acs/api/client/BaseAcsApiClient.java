package com.touchatag.acs.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class BaseAcsApiClient {

	private static String XML_PREFIX = "<?xml version=\"1.0\" ?>";
	public static int HTTP_TIMEOUT = 6000;
	public static String CALLBACK_URL = "touchatag://callback";
	public static String ENCODING = "utf-8";

	protected String baseURI;
	protected HttpClient httpClient;
	protected OAuthConsumer consumer;

	protected BaseAcsApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		URI uri = URI.create(server.getUrl() + "/tikitag-web/rest");
		int port = uri.getPort();
		baseURI = uri.getScheme() + "://" + uri.getHost() + (port != -1 ? ":" + port : "") + "/tikitag-web/rest/api";
		consumer = new CommonsHttpOAuthConsumer(server.getKey(), server.getSecret());
		consumer.setTokenWithSecret(accessToken, accessTokenSecret);
		createHttpClient(uri);
	}
	
	protected void createHttpClient(URI uri){
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

	public boolean ping() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpGet httpGet = new HttpGet(baseURI + "/ping");
		HttpResponse response = connect(httpGet);
		processResponse(response, 200);
		return true;
	}

	protected boolean processResponse(HttpResponse response, int expectedHttpCode) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		int statusCode = getStatusCode(response);
		if (statusCode == expectedHttpCode) {
			return true;
		} else if (statusCode == 500) {
			throw new AcsApiException();
		} else if (statusCode == 0) {
			throw new NoInternetException();
		} else {
			throw new UnexpectedHttpResponseCodeException();
		}
	}

	protected HttpResponse connect(HttpUriRequest request) {
		try {
			consumer.sign(request);
			String content = "NO CONTENT";
			if (request instanceof HttpEntityEnclosingRequest && ((HttpEntityEnclosingRequest)request).getEntity() != null) {
				HttpEntityEnclosingRequest httpEntityEnclosingReq = (HttpEntityEnclosingRequest) request;
				httpEntityEnclosingReq.setEntity(new BufferedHttpEntity(httpEntityEnclosingReq.getEntity()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				httpEntityEnclosingReq.getEntity().writeTo(baos);
				content = "CONTENT[" + prettyFormatXml(baos.toString(ENCODING)) + "]";
			}
			log(">>> " + request.getMethod() + " " + request.getURI().toString() + " " + content);

			HttpResponse response = httpClient.execute(request);
			content = "NO CONTENT";
			if (response.getEntity() != null) {
				response.setEntity(new BufferedHttpEntity(response.getEntity()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				response.getEntity().writeTo(baos);
				content = "CONTENT[" + prettyFormatXml(baos.toString(ENCODING)) + "]";
			}
			log("<<< " + response.getStatusLine().getStatusCode() + " " + content);
			return response;
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
	
	private String prettyFormatXml(String xml){
		return xml.replaceAll(">", ">\n");
		
	}
	
	protected abstract void log(String message);

	protected int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	protected String getResponseBody(HttpResponse response) {
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

	public static <T> String toXml(T object) {
		try {
			Serializer serializer = new Persister();
			StringWriter writer = new StringWriter();
			serializer.write(object, writer);
			return XML_PREFIX + writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T fromXml(String xml, Class<T> clazz) {
		Serializer serializer = new Persister();
		try {
			return serializer.read(clazz, xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	protected <T> T doGet(String endpoint, Class<T> clazz) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpGet httpGet = new HttpGet(baseURI + endpoint);
		HttpResponse response = connect(httpGet);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return fromXml(body, clazz);
	}

	protected <T> T doPost(String endpoint, T object, Class<T> clazz) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPost httpPost = new HttpPost(baseURI + endpoint);
		String xml = toXml(object);
		try {
			ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		HttpResponse response = connect(httpPost);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return fromXml(body, clazz);
	}
	
	protected <T> T doPost(String endpoint, Class<T> clazz) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPost httpPost = new HttpPost(baseURI + endpoint);
		
		HttpResponse response = connect(httpPost);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return fromXml(body, clazz);
	}

	protected <T> T doPut(String endpoint, T object, Class<T> clazz) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPut httpPut = new HttpPut(baseURI + endpoint);
		String xml = toXml(object);
		try {
			ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPut.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		HttpResponse response = connect(httpPut);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return fromXml(body, clazz);
	}
	
	protected <T> T doPut(String endpoint, Class<T> clazz) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPut httpPut = new HttpPut(baseURI + endpoint);

		HttpResponse response = connect(httpPut);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return fromXml(body, clazz);
	}
	
	protected void doPut(String endpoint) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPut httpPut = new HttpPut(baseURI + endpoint);

		HttpResponse response = connect(httpPut);
		processResponse(response, 204);
	}
	
	protected boolean doDelete(String endpoint) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		HttpDelete httpDelete = new HttpDelete(baseURI + endpoint);
		HttpResponse response = connect(httpDelete);
		processResponse(response, 204);
		return true;
	}

}
