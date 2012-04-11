package com.touchatag.beta.activity.connections;

import org.apache.http.client.methods.HttpUriRequest;

import com.touchatag.beta.store.SettingsStore;

public interface ConnectionAuthorizer {

	public void authorize(String authorizeUrl);
	
	public void retrieveToken(HttpUriRequest httpUriRequest);
	
	public void storeAccessToken(Connection connection, String accessToken);
	
	public SettingsStore getSettingsStore();
	
}
