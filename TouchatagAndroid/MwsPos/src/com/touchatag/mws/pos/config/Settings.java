package com.touchatag.mws.pos.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	private static final String SETTINGS = "mws-pos-settings";
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String ACCESSTOKEN = "token";
	public static final String ACCESSTOKENSECRET = "secret";
	
	private SharedPreferences preferences;
	
	public Settings(Context ctx){
		preferences = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
	}
	
	public void setUsername(String username){
		preferences.edit().putString(USERNAME, username).commit();
	}
	
	public void setPassword(String password){
		preferences.edit().putString(PASSWORD, password).commit();
	}
	
	public void setAccessToken(String accessToken){
		preferences.edit().putString(ACCESSTOKEN, accessToken).commit();
	}
	
	public void setAccessTokenSecret(String accessTokenSecret){
		preferences.edit().putString(ACCESSTOKENSECRET, accessTokenSecret).commit();
	}
	
	public String getUsername(){
		return preferences.getString(USERNAME, null);
	}
	
	public String getPassword(){
		return preferences.getString(PASSWORD, null);
	}
	
	public String getAccessToken(){
		return preferences.getString(ACCESSTOKEN, null);
	}
	
	public String getAccessTokenSecret(){
		return preferences.getString(ACCESSTOKENSECRET, null);
	}
	
}
