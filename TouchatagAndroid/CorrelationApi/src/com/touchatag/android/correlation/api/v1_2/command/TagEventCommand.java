package com.touchatag.android.correlation.api.v1_2.command;

import com.touchatag.android.correlation.api.v1_2.AdapterUtils;
import com.touchatag.android.correlation.api.v1_2.model.SoapBody;
import com.touchatag.android.correlation.api.v1_2.model.SoapEnvelope;
import com.touchatag.android.correlation.api.v1_2.model.TagEvent;
import com.touchatag.android.correlation.api.v1_2.model.TagEventResponse;

public class TagEventCommand extends BaseCommand<TagEvent, TagEventResponse> {
	
	private static String XML_PREFIX = "<?xml version=\"1.0\" ?>";
	
	public TagEventCommand(TagEvent request, String username, String password) {
		super(request, username, password);
	}

	@Override
	protected TagEventResponse deserializeResponse(String responseBody) {
		SoapEnvelope envelope = AdapterUtils.fromXml(responseBody, SoapEnvelope.class);
		return envelope.body.getTagEventResponse();
	}


}
