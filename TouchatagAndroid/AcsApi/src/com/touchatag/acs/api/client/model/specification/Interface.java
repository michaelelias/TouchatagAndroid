package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element(name="interface")
public class Interface implements Serializable {

	@ElementList(required=false)
	List<Pin> pins;
}
