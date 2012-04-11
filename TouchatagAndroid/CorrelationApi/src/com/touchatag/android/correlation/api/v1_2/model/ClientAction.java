package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;

@Element
public class ClientAction {

	@Element
	private Container container;

	public Container getContainer() {
		return container;
	}
	
}
