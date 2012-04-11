package com.touchatag.mws.pos.rest.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class Party {

	public enum PartyType {

		PARTICIPANT, AFFILIATE;

	}

	@Attribute
	private String id;
	
	@Attribute
	private String displayName;
	
	@Attribute
	private String userName;
	
	@Attribute
	private PartyType type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PartyType getType() {
		return type;
	}

	public void setType(PartyType type) {
		this.type = type;
	}

}
