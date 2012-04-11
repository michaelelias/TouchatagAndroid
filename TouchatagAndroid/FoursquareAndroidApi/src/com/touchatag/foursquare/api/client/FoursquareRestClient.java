package com.touchatag.foursquare.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import com.touchatag.foursquare.api.client.adapter.VenueAdapter;
import com.touchatag.foursquare.api.client.adapter.VenueListAdapter;
import com.touchatag.foursquare.api.client.model.Checkin;
import com.touchatag.foursquare.api.client.model.Venue;

public abstract class FoursquareRestClient {

	private static String ENCODING = "utf-8";
	public static int HTTP_TIMEOUT = 6000;
	private static final String BASE_URL = "https://api.foursquare.com/v2/";
	private static final String CLIENT_ID = "FGBJ11X3B4JNIMRA1HBO3OYNN3WBIFSYUAPW5GVZAVQTDT3X";
	private static final String CLIENT_SECRET = "SQVEPFJB1ZRTLYASBJVUHTWX4M00OSYXKS3SQ05DHGWR03L0";
	private HttpClient httpClient;
	
	private String token;

	public FoursquareRestClient() {
		URI uri = URI.create(BASE_URL);
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
	
	public void setToken(String token){
		this.token = token;
	}

	public Venue findVenueById(String venueId) {
		String url = BASE_URL + "venues/" + venueId;
		HttpGet httpGet = new HttpGet(appendOAuthConsumerInfo(url));

		try {
			HttpResponse response = execute(httpGet);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {
				return new VenueAdapter().fromJSON(responseBody);
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Venue> searchVenues(String latitude, String longitude, String query) {
		query = URLEncoder.encode(query);
		String coordinates = latitude + "," + longitude;
		String url = BASE_URL + "venues/search?ll=" + coordinates + "&query=" + query + "&v=20110701";
		HttpGet httpGet = new HttpGet(appendOAuthConsumerInfo(url));
		try {
			HttpResponse response = execute(httpGet);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {
				return new VenueListAdapter().fromJSON(responseBody);
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Venue>();
	}

	public void getActingUser() {
		String url = BASE_URL + "users/self";
		HttpGet httpGet = new HttpGet(appendAccessToken(url));
		try {
			HttpResponse response = execute(httpGet);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Checkin addCheckin(String venueId, String shout, String broadcast, String latitude, String longitude) {
		String url = BASE_URL + "checkins/add?venueId={0}&shout={1}&broadcast={2}&ll={3}";
		url = MessageFormat.format(url, venueId, shout, broadcast, latitude + "," + longitude);
		HttpPost httpPost = new HttpPost(appendAccessToken(url));
		try {
			HttpResponse response = execute(httpPost);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException {
		logRequest(httpUriRequest);
		HttpResponse httpResponse = httpClient.execute(httpUriRequest);
		logResponse(httpResponse);
		return httpResponse;
	}

	private String appendOAuthConsumerInfo(String url) {
		URI uri = URI.create(url);
		String query = uri.getQuery();
		if(query == null || query.length() == 0){
			url += "?";
		} else {
			url += "&";
		}
		return url + "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;
	}
	
	private String appendAccessToken(String url){
		if(token == null){
			throw new RuntimeException("No Access Token has been set in the client");
		}
		URI uri = URI.create(url);
		String query = uri.getQuery();
		if(query == null || query.length() == 0){
			url += "?";
		} else {
			url += "&";
		}
		return url + "oauth_token=" + token;
	}

	private void logRequest(HttpUriRequest httpUriRequest) {
		try {
			String content = "NO CONTENT";
			if (httpUriRequest instanceof HttpEntityEnclosingRequest && ((HttpEntityEnclosingRequest) httpUriRequest).getEntity() != null) {
				HttpEntityEnclosingRequest httpEntityEnclosingReq = (HttpEntityEnclosingRequest) httpUriRequest;
				httpEntityEnclosingReq.setEntity(new BufferedHttpEntity(httpEntityEnclosingReq.getEntity()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				httpEntityEnclosingReq.getEntity().writeTo(baos);
				content = "CONTENT[" + prettyFormatXml(baos.toString(ENCODING)) + "]";
			}
			log(">>> " + httpUriRequest.getMethod() + " " + httpUriRequest.getURI().toString() + " " + content);
		} catch (Exception e) {
			log("Error : " + e.getMessage());
		}
	}

	private void logResponse(HttpResponse httpResponse) {
		try {
			String content = "NO CONTENT";
			if (httpResponse.getEntity() != null) {
				httpResponse.setEntity(new BufferedHttpEntity(httpResponse.getEntity()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(baos);
				content = "CONTENT[" + prettyFormatXml(baos.toString(ENCODING)) + "]";
			}
			log("<<< " + httpResponse.getStatusLine().getStatusCode() + " " + content);
		} catch (Exception e) {
			log("Error : " + e.getMessage());
		}
	}

	public abstract void log(String message);

	private String prettyFormatXml(String xml) {
		return xml.replaceAll(">", ">\n");
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
}
