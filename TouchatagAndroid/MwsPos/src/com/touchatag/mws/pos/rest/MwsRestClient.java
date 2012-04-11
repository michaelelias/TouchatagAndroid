package com.touchatag.mws.pos.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.net.Uri;

import com.touchatag.mws.pos.config.Server;
import com.touchatag.mws.pos.rest.model.Page;
import com.touchatag.mws.pos.rest.model.Transaction;
import com.touchatag.mws.pos.util.URLEncoder;

public class MwsRestClient {

	public static int HTTP_TIMEOUT = 6000;
	public static String CALLBACK_URL = "touchatag://callback";
	private static String ENCODING = "utf-8";
	private final DateFormat iso8601DateTimeFormat = new SimpleDateFormat(ISO8601_PATTERN);
	private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private String baseURI;
	private HttpClient httpClient;
	private OAuthConsumer consumer;

	private MwsRestClient(String baseURI, OAuthConsumer consumer) {
		this.baseURI = baseURI + "/rest/api";
		this.consumer = consumer;

		Uri uri = Uri.parse(this.baseURI);
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

	public static MwsRestClient create(Server server, String accessToken, String accessTokenSecret) {
		String serverEndpoint = server.getUrl();
		Uri uri = Uri.parse(serverEndpoint);
		int port = uri.getPort();
		String baseURL = uri.getScheme() + "://" + uri.getHost() + (port != -1 ? ":" + port : "");

		CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(server.getKey(), server.getSecret());
		consumer.setTokenWithSecret(accessToken, accessTokenSecret);
		return new MwsRestClient(baseURL, consumer);
	}

	public Page<Transaction> getTransactions(String locationId, Date startDate, Date endDate, int pageNumber, int pageSize) {
		String url = baseURI + "/organizations/my/affiliates/me/transactions/page/{3}?pageSize={4}";
		if (locationId != null) {
			url += "&locationId={0}";
		}
		if (startDate != null) {
			url += "&startDate={1}";
		}
		if (endDate != null) {
			url += "&endDate={2}";
		}
		HttpGet request = createHttpGet(url, locationId, (startDate != null) ? iso8601DateTimeFormat.format(startDate) : null,
				(endDate != null) ? iso8601DateTimeFormat.format(endDate) : null, pageNumber, pageSize);
		HttpResponse response = this.connect(request);
		if(processResponse(response, 200)){
			String responseBody = getResonseBody(response);
			Serializer serializer = new Persister();
			try {
				Page page = serializer.read(Page.class, responseBody);
				System.out.println(page);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private boolean processResponse(HttpResponse response, int expectedHttpCode) {
		int statusCode = getStatusCode(response);
		if (statusCode == expectedHttpCode) {
			return true;
		} else if (statusCode == 500) {
			//throw new AcsApiException(ErrorDeserializer.deserialize(getResonseBody(response)));
		} else if (statusCode == 0) {
			//throw new NoInternetException();
		} else {
			//throw new UnexpectedHttpResponseCode(statusCode);
		}
		return false;
	}

	private HttpResponse connect(HttpUriRequest req) {
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

	private int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	private String getResonseBody(HttpResponse response) {
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

	private static HttpPost createHttpPost(String tokenizedUrl, Object... params) {
		return new HttpPost(URLEncoder.encodeParameters(tokenizedUrl, params));
	}

	private static HttpPut createHttpPut(String tokenizedUrl, Object... params) {
		return new HttpPut(URLEncoder.encodeParameters(tokenizedUrl, params));
	}

	private static HttpGet createHttpGet(String tokenizedUrl, Object... params) {
		return new HttpGet(URLEncoder.encodeParameters(tokenizedUrl, params));
	}

	private static HttpDelete createHttpDelete(String tokenizedUrl, Object... params) {
		return new HttpDelete(URLEncoder.encodeParameters(tokenizedUrl, params));
	}

}
