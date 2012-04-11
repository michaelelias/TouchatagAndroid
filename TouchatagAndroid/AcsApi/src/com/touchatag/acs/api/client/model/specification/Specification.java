package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(strict=false)
@Namespace(reference="http://acs.touchatag.com/schema/specification-1.1")
public class Specification implements Serializable {

	@Element
	public Application application;
	
	@Element
	public Blueprint blueprint;
	
	public Specification(){
		application = new Application();
		blueprint = new Blueprint();
	}
}
