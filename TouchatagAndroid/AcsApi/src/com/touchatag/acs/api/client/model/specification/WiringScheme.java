package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element
public class WiringScheme implements Serializable {

	@ElementList(inline=true, required=false, entry="wire")
	public List<Wire> wires = new ArrayList<Wire>();
	
	@ElementList(inline=true, required=false, entry="shunt")
	public List<Wire> shunts = new ArrayList<Wire>();
	
}
