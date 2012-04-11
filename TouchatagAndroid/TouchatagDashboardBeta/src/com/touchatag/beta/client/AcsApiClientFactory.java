package com.touchatag.beta.client;

import android.util.Log;

import com.touchatag.acs.api.client.AcsIdentityApiClient;
import com.touchatag.acs.api.client.ApplicationApiClient;
import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.MetadataApiClient;
import com.touchatag.acs.api.client.TagApiClient;
import com.touchatag.beta.store.SettingsStore;

public class AcsApiClientFactory {

	public static TagApiClient createTagApiClient(SettingsStore settingsStore){
		return new TagApiClient(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret()) {
			
			@Override
			protected void log(String message) {
				Log.i(TagApiClient.class.getSimpleName(), message);
			}
		};
	}
	
	public static ApplicationApiClient createApplicationApiClient(SettingsStore settingsStore){
		return new ApplicationApiClient(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret()) {
			
			@Override
			protected void log(String message) {
				Log.i(ApplicationApiClient.class.getSimpleName(), message);
			}
		};
	}
	
	public static CorrelationDefinitionApiClient createCorrelationDefinitionApiClient(SettingsStore settingsStore){
		return new CorrelationDefinitionApiClient(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret()) {
			
			@Override
			protected void log(String message) {
				Log.i(CorrelationDefinitionApiClient.class.getSimpleName(), message);
			}
		};
	}
	
	public static MetadataApiClient createMetadataApiClient(SettingsStore settingsStore){
		return new MetadataApiClient(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret()) {
			
			@Override
			protected void log(String message) {
				Log.i(MetadataApiClient.class.getSimpleName(), message);
			}
		};
	}
	
	public static AcsIdentityApiClient createAcsIdentityApiApiClient(SettingsStore settingsStore){
		return new AcsIdentityApiClient(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret()) {
			
			@Override
			protected void log(String message) {
				Log.i(AcsIdentityApiClient.class.getSimpleName(), message);
			}
		};
	}
}
