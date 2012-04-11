package com.touchatag.mws.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class Field {
	
	@Attribute
	private String validationPattern;
	
	@Attribute
	private boolean unique;
	
	@Attribute
	private boolean required;
	
	@Attribute
	private String name;
	
	@Attribute
	private String description;
	
}
