package com.touchatag.beta.client.soap.command;

import com.touchatag.beta.client.soap.model.request.GetUser;
import com.touchatag.beta.client.soap.model.response.GetUserResponse;
import com.touchatag.beta.client.soap.serialization.GetUserResponseDeserializer;
import com.touchatag.beta.client.soap.serialization.GetUserSerializer;

public class GetUserCommand extends BaseCommand<GetUser, GetUserResponse> {

	public GetUserCommand(GetUser request, String username, String password) {
		super(request, username, password);
	}
	
	@Override
	protected String serializeRequest(GetUser requestDTO) {
		return GetUserSerializer.serialize(requestDTO);
	}

	@Override
	protected GetUserResponse deserializeResponse(String responseBody) {
		return GetUserResponseDeserializer.deserialize(responseBody);
	}

}
