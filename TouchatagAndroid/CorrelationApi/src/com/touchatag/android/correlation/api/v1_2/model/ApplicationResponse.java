package com.touchatag.android.correlation.api.v1_2.model;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ApplicationResponse implements Serializable{

	@Attribute
	private String identifier;
	
	@Element(name="ClientAction")
	private ClientAction clientAction;

	public String getIdentifier() {
		return identifier;
	}

	public ClientAction getClientAction() {
		return clientAction;
	}
	
}
