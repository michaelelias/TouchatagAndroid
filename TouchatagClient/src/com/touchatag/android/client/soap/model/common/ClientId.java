package com.touchatag.android.client.soap.model.common;

import java.io.Serializable;


public class ClientId implements Serializable{

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String clientId) {
		this.id = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String clientName) {
		this.name = clientName;
	}

}
