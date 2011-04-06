package com.touchatag.android.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
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
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.Uri;

import com.touchatag.android.client.rest.model.AcsApiException;
import com.touchatag.android.client.rest.model.Application;
import com.touchatag.android.client.rest.model.ClaimingRule;
import com.touchatag.android.client.rest.model.CorrelationDefinition;
import com.touchatag.android.client.rest.model.Page;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.client.rest.serialization.ApplicationAdapter;
import com.touchatag.android.client.rest.serialization.CorrelationDefinitionAdapter;
import com.touchatag.android.client.rest.serialization.ErrorDeserializer;
import com.touchatag.android.client.rest.serialization.PageDeserializer;
import com.touchatag.android.client.rest.serialization.TagDeserializer;
import com.touchatag.android.store.Server;

public class TouchatagRestClient {

	public static String CALLBACK_URL = "touchatag://callback";
	private static String ENCODING = "utf-8";
	private String baseURI;
	private HttpClient httpClient;
	private OAuthConsumer consumer;

	private TouchatagRestClient(String baseURI, OAuthConsumer consumer) {
		this.baseURI = baseURI + "/tikitag-web/rest/api";
		this.consumer = consumer;
		httpClient = new DefaultHttpClient();
	}

	public static TouchatagRestClient create(Server server, String accessToken, String accessTokenSecret) {
		String serverEndpoint = server.getUrl();
		Uri uri = Uri.parse(serverEndpoint);
		int port = uri.getPort();
		String baseURL = uri.getScheme() + "://" + uri.getHost() + (port != -1 ? ":" + port : "");

		CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(server.getKey(), server.getSecret());
		consumer.setTokenWithSecret(accessToken, accessTokenSecret);
		return new TouchatagRestClient(baseURL, consumer);
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

	public boolean ping() {
		HttpGet httpGet = new HttpGet(baseURI + "/ping");
		HttpResponse response = connect(httpGet);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);

		return statusCode == 200;
	}

	public Page<Tag> getTags(int pageNumber, int pageSize) {
		HttpGet httpGet = new HttpGet(baseURI + "/tags/page/" + pageNumber + "?pageSize=" + pageSize);
		HttpResponse response = connect(httpGet);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);

		if (statusCode == 200) {
			return (Page<Tag>) PageDeserializer.deserialize(responseBody);
		}
		return null;
	}

	public Page<Application> getApplications(int pageNumber, int pageSize) {
		HttpGet httpGet = new HttpGet(baseURI + "/applications/page/" + pageNumber + "?pageSize=" + pageSize);
		HttpResponse response = connect(httpGet);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);

		if (statusCode == 200) {
			return (Page<Application>) PageDeserializer.deserialize(responseBody);
		}
		return null;
	}

	public Application createApplication(Application app) {
		try {
			HttpPost httpPost = new HttpPost(baseURI + "/applications");

			ApplicationAdapter adapter = new ApplicationAdapter();
			String appXml = adapter.serialize(app);
			ByteArrayEntity entity = new ByteArrayEntity(appXml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPost.setEntity(entity);

			HttpResponse response = connect(httpPost);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {
				return adapter.deserialize(responseBody);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Application updateApplication(Application app) {
		try {
			HttpPut httpPut = new HttpPut(baseURI + "/applications/" + app.getId());

			ApplicationAdapter adapter = new ApplicationAdapter();
			String appXml = adapter.serialize(app);
			ByteArrayEntity entity = new ByteArrayEntity(appXml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPut.setEntity(entity);

			HttpResponse response = connect(httpPut);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {
				return adapter.deserialize(responseBody);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteApplication(Application app) {
		HttpDelete httpDelete = new HttpDelete(baseURI + "/applications/" + app.getId());

		HttpResponse response = connect(httpDelete);
		int statusCode = getStatusCode(response);
		switch (statusCode) {
		case 204:
		case 404:
			return true;
		}
		return false;
	}

	public CorrelationDefinition getCorrelationDefinition() {
		HttpGet httpGet = new HttpGet(baseURI + "/correlationDefinition");
		HttpResponse response = connect(httpGet);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);

		if (statusCode == 200) {
			CorrelationDefinitionAdapter adapter = new CorrelationDefinitionAdapter();
			return adapter.deserialize(responseBody);
		}
		return null;
	}

	public CorrelationDefinition updateCorrelationDefinition(CorrelationDefinition corrDef) {
		try {
			HttpPost httpPost = new HttpPost(baseURI + "/correlationDefinition");

			CorrelationDefinitionAdapter adapter = new CorrelationDefinitionAdapter();
			String corrDefXml = adapter.serialize(corrDef);
			ByteArrayEntity entity = new ByteArrayEntity(corrDefXml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPost.setEntity(entity);

			HttpResponse response = connect(httpPost);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);

			if (statusCode == 200) {
				return adapter.deserialize(responseBody);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Tag acquireTag(String clientName, int timeout) throws AcsApiException {
		String url = baseURI + "/acquirements/rfid-tag/for/me/at/me";
		if (clientName.length() > 0) {
			url += "on/" + clientName;
		}
		url += "?timeout=" + timeout;
		HttpPut httpPut = new HttpPut(url);
		HttpResponse response = connect(httpPut);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);
		switch (statusCode) {
		case 200:
			TagDeserializer tagDeserializer = new TagDeserializer();
			return tagDeserializer.deserialize(responseBody);
		case 500:
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
		return null;
	}
	
	public Tag generateQrTag() throws AcsApiException {
		String url = baseURI + "/tags?type=QR";
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = connect(httpPost);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);
		switch (statusCode) {
		case 200:
			TagDeserializer tagDeserializer = new TagDeserializer();
			return tagDeserializer.deserialize(responseBody);
		case 500:
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
		return null;
	}

	public Tag getTag(String tagHash) throws AcsApiException {
		HttpGet httpGet = new HttpGet(baseURI + "/tags/" + tagHash);
		HttpResponse response = connect(httpGet);
		int statusCode = getStatusCode(response);
		String responseBody = getResonseBody(response);
		switch (statusCode) {
		case 200:
			TagDeserializer tagDeserializer = new TagDeserializer();
			return tagDeserializer.deserialize(responseBody);
		case 500:
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
		return null;
	}

	public boolean deleteTag(String tagHash) throws AcsApiException {
		HttpDelete httpDelete = new HttpDelete(baseURI + "/tags/" + tagHash);
		HttpResponse response = connect(httpDelete);
		int statusCode = getStatusCode(response);
		switch (statusCode) {
		case 204:
			return true;
		case 500:
			String responseBody = getResonseBody(response);
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
		return false;
	}

	public ClaimingRule setClaimingRule(String tagHash, ClaimingRule rule) throws AcsApiException {
		try {
			HttpPut httpPut = new HttpPut(baseURI + "/tags/" + tagHash + "/claimingrule");

			ByteArrayEntity entity = new ByteArrayEntity(rule.name().getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPut.setEntity(entity);

			HttpResponse response = connect(httpPut);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);
			switch (statusCode) {
			case 200:
				return ClaimingRule.valueOf(responseBody);
			case 500:
				throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void disableTag(String tagHash) throws AcsApiException {
		HttpPut httpPut = new HttpPut(baseURI + "/tags/" + tagHash + "/disable");

		HttpResponse response = connect(httpPut);
		int statusCode = getStatusCode(response);
		switch (statusCode) {
		case 204:
			return;
		case 500:
			String responseBody = getResonseBody(response);
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
	}
	
	public void enableTag(String tagHash) throws AcsApiException {
		HttpPut httpPut = new HttpPut(baseURI + "/tags/" + tagHash + "/enable");

		HttpResponse response = connect(httpPut);
		int statusCode = getStatusCode(response);
		switch (statusCode) {
		case 204:
			return;
		case 500:
			String responseBody = getResonseBody(response);
			throw new AcsApiException(ErrorDeserializer.deserialize(responseBody));
		}
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

}
