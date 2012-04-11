package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class Block implements Serializable {

	@ElementList(inline=true)
	public List<Property> properties = new ArrayList<Property>();
	
	@Attribute
	public String id;
	
	@Attribute
	public String ref;
	
}
