package com.touchatag.android.correlation.api.v1_2.command;

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

import com.touchatag.android.correlation.api.v1_2.AdapterUtils;
import com.touchatag.android.correlation.api.v1_2.model.SoapBody;
import com.touchatag.android.correlation.api.v1_2.model.SoapEnvelope;

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
	private Logger logger;

	public BaseCommand(REQUEST request, String username, String password) {
		this.request = request;
		this.username = username;
		this.password = password;
	}

	/**
	 * Executes this command using the given httpclient. A boolean is returned
	 * indicating the command was executed successfully. A successful execution
	 * only means a request was sent and a response received. 
	 * Use {@link #getResponseBody()} and {@link #getResponseHttpStatusCode()}}
	 * to check if the command was successful in an application context.
	 * 
	 * @param httpClient
	 * @return
	 * @throws InvalidCredentialsException 
	 * @throws SoapFaultException 
	 */
	public boolean execute(DefaultHttpClient httpClient, URI endpoint) throws InvalidCredentialsException, SoapFaultException {
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
			
			log(">>> " + httpPost.getMethod() + " " + httpPost.getURI().toString());
			log(">>> " + requestBody);
			
			timeRequestSent = System.currentTimeMillis();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			timeResponseReceived = System.currentTimeMillis();
			responseHttpStatusCode = httpResponse.getStatusLine().getStatusCode();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpResponse.getEntity());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bufferedHttpEntity.writeTo(baos);
			responseBody = baos.toString("UTF-8");
			
			log("<<< " + httpPost.getMethod() + " " + httpPost.getURI().toString());
			log("<<< " + httpResponse.getStatusLine().getStatusCode() + responseBody);
			
			switch(responseHttpStatusCode){
			case 200 : 
				response = deserializeResponse(responseBody);
				break;
			case 401 :
				throw new InvalidCredentialsException();
			case 500 :
				SoapEnvelope envelope = AdapterUtils.fromXml(responseBody, SoapEnvelope.class);
				throw new SoapFaultException(envelope.body.getFault());
			default :
				throw new RuntimeException("Unexpected Http status code : " + responseHttpStatusCode);
			}

			
			
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

	protected String serializeRequest(REQUEST requestDTO){
		SoapBody soapBody = new SoapBody(requestDTO);
		SoapEnvelope soapEvelope = new SoapEnvelope(soapBody);
		return AdapterUtils.toXml(soapEvelope);
	}

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

	private void log(String message){
		if(logger != null){
			logger.log(message);
		}
	}
	
	public void setLogger(Logger logger){
		this.logger = logger;
	}
	
	public interface Logger {
		
		public void log(String message);
		
	}
	
}
