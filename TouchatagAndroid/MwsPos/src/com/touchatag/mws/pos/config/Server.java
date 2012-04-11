package com.touchatag.mws.pos.config;


public enum Server {

	BETA("Beta server",	"https://ec2-46-51-156-92.eu-west-1.compute.amazonaws.com", "7fbe-7d46-5e59-4dfa-a042-8654-38d3-fd23", "caOZB3o5Xk2K8GJYU4DfsI5yexFohjI5"); //

	private String name;
	private String url;
	private String key;
	private String secret;

	private Server(String name, String url, String key, String secret) {
		this.name = name;
		this.url = url;
		this.key = key;
		this.secret = secret;
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

}
