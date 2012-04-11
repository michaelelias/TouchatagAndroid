package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class Application implements Serializable{

	@Attribute(required=false)
	public EventModel eventModel = EventModel.PUT_TOUCH;
	
	@ElementList(required=true, inline=true)
	public List<Command> commands = new ArrayList<Command>();
}
