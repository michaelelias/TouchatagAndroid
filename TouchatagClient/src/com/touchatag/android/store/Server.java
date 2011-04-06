package com.touchatag.android.store;


public class Server {

//	TOUCHATAG("Touchatag server", "https://acs.touchatag.com", null, null), //
//	PRESALES3("Presales 3", "https://presales3.ttag.be", "2b81-633c-9511-487f-82d9-702b-4559-7ec7", "CwoPyeN0JNl9DrpsVZbIU3AIUgz6vTAm"), //
//	LOCAL_EMULATOR("Local server for emulator",	"http://10.0.2.2:8080", "25f1-4e7e-ec4c-4680-8133-a8cd-47c1-759e", "cWPHxlsdtmOfHLXVT4s7XxqB7IwZhNpz"), //
//	NG_CONNECT("NG Connect", "http://ngconnect.ttag.be", null, null);

	private String name;
	private String url;
	private String key;
	private String secret;

	public Server(String name, String url, String key, String secret) {
		this.name = name;
		this.url = url;
		this.key = key;
		this.secret = secret;
	}

//	public static String[] getEntries() {
//		String[] entries = new String[Server.values().length];
//		int i = 0;
//		for (Server server : values()) {
//			entries[i] = server.getName();
//			i++;
//		}
//		return entries;
//	}
//
//	public static String[] getEntryValues() {
//		String[] values = new String[Server.values().length];
//		int i = 0;
//		for (Server server : values()) {
//			values[i] = server.getUrl();
//			i++;
//		}
//		return values;
//	}
	
//	public static Server findByUrl(String url){
//		for (Server server : values()) {
//			if(server.getUrl().equals(url)){
//				return server;
//			}
//		}
//		return null;
//	}

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
