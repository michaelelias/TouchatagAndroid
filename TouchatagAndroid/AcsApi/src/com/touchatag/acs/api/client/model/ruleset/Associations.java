package com.touchatag.acs.api.client.model.ruleset;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root
@Namespace(prefix = "ns2", reference = "http://acs.touchatag.com/schema/associations-1.0")
public class Associations {

	@ElementList(required=false, inline=true, entry="asso")
	private List<Association> associations = new ArrayList<Association>();

	public List<Association> getAssociations() {
		return associations;
	}

	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}
	
}
