package com.touchatag.android.store;

import org.apache.http.client.methods.HttpGet;

import com.touchatag.android.R;
import com.touchatag.android.client.CorrelationGateway;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStore {

	private static final String SETTINGS = "tt-settings";

	private String prefUsername;
	private String prefPassword;
	private String prefClientName;
	private String prefAutoLaunch;
	private String prefServer;
	private String prefAccessToken = "accesstoken";
	private String prefAccessTokenSecret = "accesstokensecret";

	private Context ctx;
	
	private ServerStore serverStore;

	public SettingsStore(Context ctx) {
		this.ctx = ctx;
		prefUsername = ctx.getString(R.string.pref_username);
		prefPassword = ctx.getString(R.string.pref_password);
		prefClientName = ctx.getString(R.string.pref_clientname);
		prefAutoLaunch = ctx.getString(R.string.pref_client_autolaunch);
		prefServer = ctx.getString(R.string.pref_client_endpoint);
	}

	public boolean hasCredentials() {
		return getUsername().length() > 0 && getPassword().length() > 0;
	}

	private SharedPreferences getPreferences() {
		return ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
	}

	public void storeUsername(String username) {
		getPreferences().edit().putString(prefUsername, username).commit();
	}

	public void storePassword(String password) {
		getPreferences().edit().putString(prefPassword, password).commit();
	}
	
	public void storeClientName(String clientName) {
		getPreferences().edit().putString(prefClientName, clientName).commit();
	}

	public String getUsername() {
		return getPreferences().getString(prefUsername, "");
	}

	public String getPassword() {
		return getPreferences().getString(prefPassword, "");
	}

	public String getClientName() {
		return getPreferences().getString(prefClientName, "");
	}
	
	public boolean isAutoLaunch() {
		return getPreferences().getBoolean(prefAutoLaunch, true);
	}
	
	public void setAutoLaunch(boolean autoLaunch){
		getPreferences().edit().putBoolean(prefAutoLaunch, autoLaunch).commit();
	}
	
	public void clearCredentials(){
		getPreferences().edit().remove(prefUsername).remove(prefPassword).commit();
	}
	
	public void revokeAuthorization(){
		getPreferences().edit().remove(prefAccessToken).remove(prefAccessTokenSecret).commit();
	}
	
	public String getServerEndpoint(){
		return getPreferences().getString(prefServer, CorrelationGateway.SERVER_ENDPOINT);
	}
	
	public void setServerEndpoint(String endpoint){
		getPreferences().edit().putString(prefServer, endpoint) ;
	}
	
	public String getAccessToken(){
		return getPreferences().getString(prefAccessToken, null);
	}
	
	public void setAccessToken(String token){
		getPreferences().edit().putString(prefAccessToken, token).commit();
	}
	
	public boolean isAuthorized(){
		return getAccessToken() != null;
	}
	
	public String getAccessTokenSecret(){
		return getPreferences().getString(prefAccessTokenSecret, null);
	}
	
	public void setAccessTokenSecret(String tokenSecret){
		getPreferences().edit().putString(prefAccessTokenSecret, tokenSecret).commit();
	}
	
	public Server getServer(){
		if(serverStore == null){
			serverStore = new ServerStore(ctx);
		}
		return serverStore.findByUrl(getServerEndpoint());
		
	}
}
