package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class Command  implements Serializable {
	
	@Attribute
	public String name;
	
	@Attribute
	public String id;
	
}
