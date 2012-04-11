package com.touchatag.beta.client.soap.command;

import com.touchatag.beta.client.soap.model.request.TagEvent;
import com.touchatag.beta.client.soap.model.response.TagEventFeedback;
import com.touchatag.beta.client.soap.serialization.TagEventResponseDeserializer;
import com.touchatag.beta.client.soap.serialization.TagEventSerializer;

public class TagEventCommand extends BaseCommand<TagEvent, TagEventFeedback> {
	
	public TagEventCommand(TagEvent request, String username, String password) {
		super(request, username, password);
	}

	@Override
	protected TagEventFeedback deserializeResponse(String responseBody) {
		return TagEventResponseDeserializer.deserialize(responseBody);
	}

	@Override
	protected String serializeRequest(TagEvent requestDTO) {
		return TagEventSerializer.serialize(requestDTO);
	}



}
