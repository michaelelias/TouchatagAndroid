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

import com.touchatag.beta.client.soap.command.CreateUserCommand;
import com.touchatag.beta.client.soap.command.GetUserCommand;
import com.touchatag.beta.client.soap.model.request.CreateUser;
import com.touchatag.beta.client.soap.model.request.GetUser;
import com.touchatag.beta.client.soap.model.response.GetUserResponse;
import com.touchatag.beta.store.Server;

public class UserManagementGateway {

	public static int HTTP_TIMEOUT = 6000;
	
	private String username;
	private String password;
	private URI endpointURI;
	
	public UserManagementGateway(String username, String password, Server server) {
		this.username = username;
		this.password = password;
		
		try {
			this.endpointURI = new URI(server.getUrl() + "/soap/userManagement");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean createUser(CreateUser createUser){
		CreateUserCommand command = new CreateUserCommand(createUser, username, password);
		boolean success = command.execute(getHttpClient(), endpointURI);
		
		return success;
	}
	
	public GetUserResponse getUser(GetUser getUser){
		GetUserCommand command = new GetUserCommand(getUser, username, password);
		boolean success = command.execute(getHttpClient(), endpointURI);
		return command.getResponse();
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
