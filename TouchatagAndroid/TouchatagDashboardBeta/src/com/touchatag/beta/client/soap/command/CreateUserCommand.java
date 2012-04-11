package com.touchatag.beta.client.soap.command;

import com.touchatag.beta.client.soap.model.request.CreateUser;
import com.touchatag.beta.client.soap.model.response.CreateUserResponse;
import com.touchatag.beta.client.soap.serialization.CreateUserSerializer;

public class CreateUserCommand extends BaseCommand<CreateUser, CreateUserResponse> {

	public CreateUserCommand(CreateUser request, String username, String password) {
		super(request, username, password);
	}
	
	@Override
	protected String serializeRequest(CreateUser requestDTO) {
		return CreateUserSerializer.serialize(requestDTO);
	}

	@Override
	protected CreateUserResponse deserializeResponse(String responseBody) {
		// TODO Auto-generated method stub
		return null;
	}

}
