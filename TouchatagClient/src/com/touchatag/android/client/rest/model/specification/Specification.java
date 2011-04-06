package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Specification implements Serializable {

	public EventModel eventModel = EventModel.PUT_TOUCH;
	public List<Command> commands = new ArrayList<Command>();
	public Blueprint blueprint;
	
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<specification>");
		sb.append("<application eventModel=\"" + eventModel.name() + "\">");
		for(Command command : commands){
			sb.append(command.toXml());
		}
		sb.append("</application>");
		if(blueprint != null){
			sb.append(blueprint.toXml());
		}
		sb.append("</specification>");
		return sb.toString();
	}
	
	public enum EventModel {
		PUT_TOUCH, PUT_TOUCH_REMOVE;
	}
}
