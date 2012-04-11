package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class Shunt extends Wire implements Serializable {

	@Attribute
	public String from;

	@Attribute
	public String to;

}
