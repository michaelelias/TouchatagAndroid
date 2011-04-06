package com.touchatag.android.client.soap.model.response;

import com.touchatag.android.client.soap.command.ResponseDTO;

public class BooleanResponse implements ResponseDTO {

	private boolean response;

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

}
