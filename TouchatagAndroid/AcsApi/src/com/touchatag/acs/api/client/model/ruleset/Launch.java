package com.touchatag.acs.api.client.model.ruleset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class Launch {

	@Attribute(required=true)
	private String applicationId;
	
	@Attribute(required=false)
	private String commandId;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	
}
