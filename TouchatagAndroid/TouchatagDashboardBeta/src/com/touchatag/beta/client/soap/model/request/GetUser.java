package com.touchatag.beta.client.soap.model.request;

import com.touchatag.beta.client.soap.command.RequestDTO;

public class GetUser implements RequestDTO {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
