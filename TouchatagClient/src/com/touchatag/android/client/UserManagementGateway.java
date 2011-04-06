package com.touchatag.android.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.impl.client.DefaultHttpClient;

import com.touchatag.android.client.soap.command.CreateUserCommand;
import com.touchatag.android.client.soap.model.request.CreateUser;

public class UserManagementGateway {

	public static int HTTP_TIMEOUT = 6000;
	
	private String username;
	private String password;
	private URI endpointURI;
	
	public UserManagementGateway(String username, String password, String server) {
		this.username = username;
		this.password = password;
		
		try {
			this.endpointURI = new URI(server + "/soap/userManagement");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean createUser(CreateUser createUser){
		CreateUserCommand command = new CreateUserCommand(createUser, username, password);
		boolean success = command.execute(getHttpClient(), endpointURI);
		return success;
	}
	
	private DefaultHttpClient getHttpClient(){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.socket.timeout", new Integer(HTTP_TIMEOUT));
		return httpClient;
	}
	
}
