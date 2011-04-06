package com.touchatag.android.client.soap.command;

import com.touchatag.android.client.soap.model.request.PingEvent;
import com.touchatag.android.client.soap.serialization.PingEventSerializer;

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
