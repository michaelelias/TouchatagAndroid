package com.touchatag.android.correlation.api.v1_2.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;

import com.touchatag.android.correlation.api.v1_2.command.ResponseDTO;

@Element(name="handleTagEventResponse")
@Namespace(prefix = "ns1", reference = "http://www.touchatag.com/acs/api/correlation-1.2")
public class TagEventResponse implements ResponseDTO {

	@Element(required = false)
	private String systemMessage;
	
	@ElementList(required=false, inline=true)
	private List<ApplicationResponse> applicationResponses;

	public String getSystemMessage() {
		return systemMessage;
	}

	public List<ApplicationResponse> getApplicationResponses() {
		return applicationResponses;
	}

}
