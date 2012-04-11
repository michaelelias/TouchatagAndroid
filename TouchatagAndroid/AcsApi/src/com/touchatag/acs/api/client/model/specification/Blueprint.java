package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;

import org.simpleframework.xml.Element;

@Element
public class Blueprint implements Serializable {

	@Element(name="superblock")
	public SuperBlock superBlock;
	
}
