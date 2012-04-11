package com.touchatag.beta.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.touchatag.beta.R;

public class SettingsStore {

	private static final String SETTINGS = "tt-settings";

	private String prefUsername;
	private String prefPassword;
	private String prefClientName;
	private String prefAutoLaunch;
	private String prefServer;
	private String prefAccessToken = "accesstoken";
	private String prefAccessTokenSecret = "accesstokensecret";
	private String prefIdentityId = "identityid";
	private String prefAccessTokenFoursquare = "accesstoken-foursquare";

	private Context ctx;
	
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
		getPreferences().edit().remove(prefUsername).remove(prefPassword).remove(prefAccessToken).remove(prefAccessTokenSecret).remove(prefIdentityId).commit();
	}
	
	public void revokeAuthorization(){
		getPreferences().edit().remove(prefAccessToken).remove(prefAccessTokenSecret).remove(prefIdentityId).commit();
	}
	
	public Server getServer(){
		return Server.valueOf(getPreferences().getString(prefServer, Server.BETA.name()));
	}
	
	public void setServer(Server server){
		getPreferences().edit().putString(prefServer, server.name()) ;
	}
	
	public String getAccessToken(){
		return getPreferences().getString(prefAccessToken, null);
	}
	
	public void setAccessToken(String token){
		getPreferences().edit().putString(prefAccessToken, token).commit();
	}
	
	public void setIdentityId(String identityId){
		getPreferences().edit().putString(prefIdentityId, identityId).commit();
	}
	
	public String getIdentityId(){
		return getPreferences().getString(prefIdentityId, null);
	}
	
	public boolean hasIdentityId(){
		return getIdentityId() != null;
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
	
	public String getAccessTokenFoursquare(){
		return getPreferences().getString(prefAccessTokenFoursquare, null);
	}
	
	public boolean hasAccessTokenFoursquare(){
		return getPreferences().getString(prefAccessTokenFoursquare, "").length() > 0;
	}
	
	public void clearAccessTokenFoursquare(){
		getPreferences().edit().remove(prefAccessTokenFoursquare).commit();
	}
	
	public void setAccessTokenFoursquare(String token){
		getPreferences().edit().putString(prefAccessTokenFoursquare, token).commit();
	}
}
