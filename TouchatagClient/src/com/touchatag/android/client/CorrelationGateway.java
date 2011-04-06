package com.touchatag.android.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.impl.client.DefaultHttpClient;

import com.touchatag.android.client.soap.command.PingCommand;
import com.touchatag.android.client.soap.command.TagEventCommand;
import com.touchatag.android.client.soap.model.request.PingEvent;
import com.touchatag.android.client.soap.model.request.TagEvent;
import com.touchatag.android.store.SettingsStore;

public class CorrelationGateway {

	public static String SERVER_ENDPOINT = "https://acs.touchatag.com";
	public static String SERVER_ENDPOINT_WSDL = "https://acs.touchatag.com/soap/correlation-1.2?wsdl";

	public static int HTTP_TIMEOUT = 6000;
	
	private String username;
	private String password;
	private URI endpointURI;

	public CorrelationGateway(String username, String password, String server) {
		this.username = username;
		this.password = password;
		
		try {
			this.endpointURI = new URI(server + "/soap/correlation-1.2");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public PingCommand ping(PingEvent pingEvent){
		PingCommand command = new PingCommand(pingEvent, username, password);
		boolean success = command.execute(getHttpClient(), endpointURI);
		return command;
	}
	
	public TagEventCommand handleTagEvent(TagEvent tagEvent) {
		TagEventCommand command = new TagEventCommand(tagEvent, username, password);
		boolean success = command.execute(getHttpClient(), endpointURI);
		return command;
	}

	private DefaultHttpClient getHttpClient(){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.socket.timeout", new Integer(HTTP_TIMEOUT));
		return httpClient;
	}
}
