package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class Breakdown implements Serializable {

	@ElementList(inline=true, required=false)
	public List<Block> blocks = new ArrayList<Block>();
	
}
