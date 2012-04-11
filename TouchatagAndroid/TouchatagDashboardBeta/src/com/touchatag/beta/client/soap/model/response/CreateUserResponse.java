package com.touchatag.beta.client.soap.model.response;

import com.touchatag.beta.client.soap.command.ResponseDTO;

public class CreateUserResponse implements ResponseDTO {

	private boolean response;

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

}
