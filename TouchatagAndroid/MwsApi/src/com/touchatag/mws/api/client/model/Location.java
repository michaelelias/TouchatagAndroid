package com.touchatag.mws.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Location {
	
	@Attribute
	private String workflow;
	
	@Attribute
	private String name;
	
	@Attribute
	private String id;
	
	@Attribute
	private boolean enabled;
	
	@Attribute
	private String description;
	
	@Element
	private Profile profile;
	
}
