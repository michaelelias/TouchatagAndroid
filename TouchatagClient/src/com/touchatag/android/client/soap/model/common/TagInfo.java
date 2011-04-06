package com.touchatag.android.client.soap.model.common;

import java.io.Serializable;


public class TagInfo implements Serializable{

	private TagId tagId;

	private byte[] tagData;

	public TagInfo(TagId tagId) {
		super();
		this.tagId = tagId;
	}

	public TagInfo(TagId tagId, byte[] tagData) {
		super();
		this.tagId = tagId;
		this.tagData = tagData;
	}

	public TagId getTagId() {
		return tagId;
	}

	public void setTagId(TagId tagId) {
		this.tagId = tagId;
	}

	public byte[] getTagData() {
		return tagData;
	}

	public void setTagData(byte[] tagData) {
		this.tagData = tagData;
	}

}
