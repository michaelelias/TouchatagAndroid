package com.touchatag.mws.pos.client;

import com.touchatag.android.correlation.api.v1_3.AcsServer;
import com.touchatag.mws.api.client.MwsServer;


public enum ServerInstance implements AcsServer, MwsServer {

//	TOUCHATAG("Touchatag server", "https://acs.touchatag.com", null, null), //
//	PRESALES3("Presales 3", "https://presales3.ttag.be", "2b81-633c-9511-487f-82d9-702b-4559-7ec7", "CwoPyeN0JNl9DrpsVZbIU3AIUgz6vTAm"), //
//	LOCAL_EMULATOR("Local server for emulator",	"http://10.0.2.2:8080", "acd1-d09b-1bb3-49f4-8f30-66b8-682a-ac44", "jJhZHdz0oOvB3ptXSxUSoX5CXYwGaTKa", "tt_android_app", "l3nal3na_1"), //
	BETA("Beta server",	"https://ec2-46-51-156-92.eu-west-1.compute.amazonaws.com", "7fbe-7d46-5e59-4dfa-a042-8654-38d3-fd23", "caOZB3o5Xk2K8GJYU4DfsI5yexFohjI5"); //

	private String name;
	private String url;
	private String key;
	private String secret;

	private ServerInstance(String name, String url, String key, String secret) {
		this.name = name;
		this.url = url;
		this.key = key;
		this.secret = secret;
	}

	public static String[] getEntries() {
		String[] entries = new String[ServerInstance.values().length];
		int i = 0;
		for (ServerInstance server : values()) {
			entries[i] = server.getName();
			i++;
		}
		return entries;
	}

	public static String[] getEntryValues() {
		String[] values = new String[ServerInstance.values().length];
		int i = 0;
		for (ServerInstance server : values()) {
			values[i] = server.name();
			i++;
		}
		return values;
	}
	
	public static ServerInstance findByUrl(String url){
		for (ServerInstance server : values()) {
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

}
