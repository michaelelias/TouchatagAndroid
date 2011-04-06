package com.touchatag.android.client.soap.model.common;

import java.io.Serializable;

public class TagId implements Serializable{

	private String identifier;

	private GenericTagType genericTagType;

	private short tagTypeCode;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public GenericTagType getGenericTagType() {
		return genericTagType;
	}

	public void setGenericTagType(GenericTagType genericTagType) {
		this.genericTagType = genericTagType;
	}

	public short getTagTypeCode() {
		return tagTypeCode;
	}

	public void setTagTypeCode(short tagTypeCode) {
		this.tagTypeCode = tagTypeCode;
	}

}
