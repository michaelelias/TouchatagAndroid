package com.touchatag.beta.client.soap.command;

import com.touchatag.beta.client.soap.model.request.PingEvent;
import com.touchatag.beta.client.soap.serialization.PingEventSerializer;

public class PingCommand extends BaseCommand<PingEvent, ResponseDTO> {

	public PingCommand(PingEvent request, String username, String password) {
		super(request, username, password);
	}

	@Override
	protected ResponseDTO deserializeResponse(String responseBody) {
		return null;
	}

	@Override
	protected String serializeRequest(PingEvent requestDTO) {
		return PingEventSerializer.serialize(getRequest());
	}

}
