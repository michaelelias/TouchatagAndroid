package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;

@Element
public class Attribute {

	@org.simpleframework.xml.Attribute
	private String name;
	
	@Element(required=false)
	private String string;

	public String getString() {
		return string;
	}

	public String getName() {
		return name;
	}

}
