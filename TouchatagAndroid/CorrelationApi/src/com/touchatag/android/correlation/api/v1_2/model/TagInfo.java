package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TagInfo {

	@Element
	private TagId tagId;

	@Element(required = false)
	private String tagData;

	public TagInfo(TagId tagId) {
		super();
		this.tagId = tagId;
	}

	public TagInfo(TagId tagId, String tagData) {
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

	public String getTagData() {
		return tagData;
	}

	public void setTagData(String tagData) {
		this.tagData = tagData;
	}

}
