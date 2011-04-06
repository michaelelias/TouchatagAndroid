package com.touchatag.android.client.soap.model.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Container implements Serializable{

	public static String TAG_MANAGEMENT = "tikitag.standard.tagManagement";
	public static String URL = "tikitag.standard.url";
	
	public static String ATTR_URL = "url";
	public static String ATTR_MESSAGE = "message";
	
	
	private String name;
	private Container container;
	private Map<String, String> attributes = new HashMap<String, String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Container getContainer() {
		return container;
	}
	public void setContainer(Container container) {
		this.container = container;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
}
