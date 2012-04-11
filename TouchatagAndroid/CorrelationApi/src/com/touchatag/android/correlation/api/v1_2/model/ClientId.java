package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ClientId {

	@Element(required=true)
	private String id;

	@Element(required=false)
	private String name;

	public ClientId(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public ClientId(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
