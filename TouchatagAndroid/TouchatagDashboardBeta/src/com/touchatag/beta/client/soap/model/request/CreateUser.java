package com.touchatag.beta.client.soap.model.request;

import com.touchatag.beta.client.soap.command.RequestDTO;
import com.touchatag.beta.client.soap.model.common.UserDTO;

public class CreateUser implements RequestDTO {

	private UserDTO user;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

}
