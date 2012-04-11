package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class TagId {

	@Text
	private String tagId;
	
	private String identifier;
	private GenericTagType genericTagType = GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT;

	public TagId(String identifier, GenericTagType genericTagType) {
		super();
		this.identifier = identifier;
		this.genericTagType = genericTagType;
		this.tagId = toTagIdentifier(genericTagType, identifier);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		this.tagId = toTagIdentifier(genericTagType, this.identifier);
	}

	public GenericTagType getGenericTagType() {
		return genericTagType;
	}

	public void setGenericTagType(GenericTagType genericTagType) {
		this.genericTagType = genericTagType;
		this.tagId = toTagIdentifier(this.genericTagType, identifier);
	}
	
	private String toTagIdentifier(GenericTagType tagType, String identifier){
		return "urn:" + tagType.toString().toLowerCase().replaceAll("_", ":") + ":" + identifier;
	}

}
