package com.touchatag.android.client.soap.model.response;

import java.io.Serializable;

public abstract class ApplicationResponse implements Serializable{

	private String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
}
