package com.touchatag.beta.client.soap.model.response;

import com.touchatag.beta.client.soap.command.ResponseDTO;
import com.touchatag.beta.client.soap.model.common.UserDTO;

public class GetUserResponse implements ResponseDTO {

	private UserDTO user;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}
	
}
