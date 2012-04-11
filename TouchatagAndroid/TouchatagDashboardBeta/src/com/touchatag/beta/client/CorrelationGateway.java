package com.touchatag.beta.client;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import com.touchatag.beta.client.soap.command.PingCommand;
import com.touchatag.beta.client.soap.command.TagEventCommand;
import com.touchatag.beta.client.soap.model.request.PingEvent;
import com.touchatag.beta.client.soap.model.request.TagEvent;
import com.touchatag.beta.store.Server;

public class CorrelationGateway {

	public static int HTTP_TIMEOUT = 6000;
	
	private String username;
	private String password;
	private URI endpointURI;

	public CorrelationGateway(String username, String password, Server server) {
		this.username = username;
		this.password = password;
		
		try {
			this.endpointURI = new URI(server.getUrl() + "/soap/correlation-1.2");
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
		DefaultHttpClient httpClient = null;
		if("https".equals(this.endpointURI.getScheme())){
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
		return httpClient;
	}
}
