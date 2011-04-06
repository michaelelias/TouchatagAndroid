package com.touchatag.android.client.soap.command;

import com.touchatag.android.client.soap.model.request.CreateUser;
import com.touchatag.android.client.soap.model.response.BooleanResponse;
import com.touchatag.android.client.soap.serialization.CreateUserSerializer;

public class CreateUserCommand extends BaseCommand<CreateUser, BooleanResponse> {

	public CreateUserCommand(CreateUser request, String username, String password) {
		super(request, username, password);
	}
	
	@Override
	protected String serializeRequest(CreateUser requestDTO) {
		return CreateUserSerializer.serialize(requestDTO);
	}

	@Override
	protected BooleanResponse deserializeResponse(String responseBody) {
		// TODO Auto-generated method stub
		return null;
	}

}
