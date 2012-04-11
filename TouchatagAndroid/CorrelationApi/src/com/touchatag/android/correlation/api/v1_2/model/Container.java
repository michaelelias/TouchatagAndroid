package com.touchatag.android.correlation.api.v1_2.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class Container {

	@org.simpleframework.xml.Attribute
	private String name;
	
	@Element(required=false)
	private Container container;
	
	@ElementList(required=false, inline=true)
	private List<Attribute> attributes;

	public String getName() {
		return name;
	}

	public Container getContainer() {
		return container;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
}
