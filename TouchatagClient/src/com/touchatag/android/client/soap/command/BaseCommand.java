package com.touchatag.android.client.soap.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

public abstract class BaseCommand<REQUEST extends RequestDTO, RESPONSE extends ResponseDTO> implements Serializable {

	private String username;
	private String password;

	private REQUEST request;
	private String requestBody;
	private Header[] requestHeaders;
	private long timeRequestSent;
	
	private RESPONSE response;
	private String responseBody;
	private String responseHeaders;
	private int responseHttpStatusCode;
	private long timeResponseReceived;


	public BaseCommand(REQUEST request, String username, String password) {
		this.request = request;
		this.username = username;
		this.password = password;
	}

	/**
	 * Executes this command using the given httpclient. A boolean is returned
	 * indicating the command was executed successfully. A successfull execution
	 * only means a request was sent and a response received. 
	 * Use {@link #getResponseBody()} and {@link #getResponseHttpStatusCode()}}
	 * to check if the command was successfull in an application context.
	 * 
	 * @param httpClient
	 * @return
	 */
	public boolean execute(DefaultHttpClient httpClient, URI endpoint) {
		try {
			HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
			httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "Basic"), new UsernamePasswordCredentials(username, password));

			HttpPost httpPost = new HttpPost(endpoint);
			httpPost.getParams().setParameter("http.socket.timeout", httpClient.getParams().getParameter("http.socket.timeout"));
			httpPost.addHeader("SOAPAction", "\"\"");
			httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");
			requestBody = serializeRequest(request);
			HttpEntity httpEntity = new StringEntity(requestBody);
			httpPost.setEntity(httpEntity);
			
			timeRequestSent = System.currentTimeMillis();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			timeResponseReceived = System.currentTimeMillis();
			responseHttpStatusCode = httpResponse.getStatusLine().getStatusCode();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpResponse.getEntity());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bufferedHttpEntity.writeTo(baos);
			responseBody = baos.toString("UTF-8");

			response = deserializeResponse(responseBody);
			return true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;

	}

	protected abstract String serializeRequest(REQUEST requestDTO);

	protected abstract RESPONSE deserializeResponse(String responseBody);

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public REQUEST getRequest() {
		return request;
	}

	public RESPONSE getResponse() {
		return response;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public Header[] getRequestHeaders() {
		return requestHeaders;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public int getResponseHttpStatusCode() {
		return responseHttpStatusCode;
	}

	public long getTimeRequestSent() {
		return timeRequestSent;
	}

	public long getTimeResponseReceived() {
		return timeResponseReceived;
	}

	
	
}
