package com.touchatag.android.correlation.api.v1_2.command;

import com.touchatag.android.correlation.api.v1_2.model.PingEvent;


public class PingCommand extends BaseCommand<PingEvent, ResponseDTO> {

	public PingCommand(PingEvent request, String username, String password) {
		super(request, username, password);
	}

	@Override
	protected ResponseDTO deserializeResponse(String responseBody) {
		return null;
	}

}
