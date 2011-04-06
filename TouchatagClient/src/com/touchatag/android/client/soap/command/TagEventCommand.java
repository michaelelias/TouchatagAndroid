package com.touchatag.android.client.soap.command;

import com.touchatag.android.client.soap.model.request.TagEvent;
import com.touchatag.android.client.soap.model.response.TagEventFeedback;
import com.touchatag.android.client.soap.serialization.TagEventResponseDeserializer;
import com.touchatag.android.client.soap.serialization.TagEventSerializer;

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
