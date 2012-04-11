package com.touchatag.beta.client.soap.model.response;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class AndroidClientActionResponse extends ApplicationResponse implements Serializable {

	private Map<String, String> parameters = new TreeMap<String, String>();

	public Map<String, String> getParameters() {
		return parameters;
	}

}
