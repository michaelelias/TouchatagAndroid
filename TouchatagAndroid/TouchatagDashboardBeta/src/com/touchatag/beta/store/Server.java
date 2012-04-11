package com.touchatag.beta.store;

import com.touchatag.acs.api.client.AcsServer;
import com.touchatag.android.correlation.api.v1_2.CorrelationServer;


public enum Server implements AcsServer, CorrelationServer {

//	TOUCHATAG("Touchatag server", "https://acs.touchatag.com", null, null), //
//	PRESALES3("Presales 3", "https://presales3.ttag.be", "2b81-633c-9511-487f-82d9-702b-4559-7ec7", "CwoPyeN0JNl9DrpsVZbIU3AIUgz6vTAm"), //
//	LOCAL_EMULATOR("Local server for emulator",	"http://10.0.2.2:8080", "acd1-d09b-1bb3-49f4-8f30-66b8-682a-ac44", "jJhZHdz0oOvB3ptXSxUSoX5CXYwGaTKa", "tt_android_app", "l3nal3na_1"), //
	BETA("Beta server",	"https://ec2-46-51-156-92.eu-west-1.compute.amazonaws.com", "d62a-ef82-24d1-4772-b314-6d92-5e0f-a927", "lPZPUhHHfZuxRf22UyVDhFYPUyH2ZzLj", "tt_android_app", "l3nal3na"); //

	private String name;
	private String url;
	private String key;
	private String secret;
	private String provisionerUsername;
	private String provisionerPassword;

	private Server(String name, String url, String key, String secret, String provisionerUsername, String provisionerPassword) {
		this.name = name;
		this.url = url;
		this.key = key;
		this.secret = secret;
		this.provisionerUsername = provisionerUsername;
		this.provisionerPassword = provisionerPassword;
	}

	public static String[] getEntries() {
		String[] entries = new String[Server.values().length];
		int i = 0;
		for (Server server : values()) {
			entries[i] = server.getName();
			i++;
		}
		return entries;
	}

	public static String[] getEntryValues() {
		String[] values = new String[Server.values().length];
		int i = 0;
		for (Server server : values()) {
			values[i] = server.name();
			i++;
		}
		return values;
	}
	
	public static Server findByUrl(String url){
		for (Server server : values()) {
			if(server.getUrl().equals(url)){
				return server;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getKey() {
		return key;
	}

	public String getSecret() {
		return secret;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getProvisionerUsername() {
		return provisionerUsername;
	}

	public String getProvisionerPassword() {
		return provisionerPassword;
	}
	
	
}
