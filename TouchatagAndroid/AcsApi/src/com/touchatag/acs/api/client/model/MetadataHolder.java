package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference="http://acs.touchatag.com/schema/metadataHolder-1.0")
public class MetadataHolder {

	@Attribute
	private String reference;
	
	@Attribute
	private MetadataHolderType type;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public MetadataHolderType getType() {
		return type;
	}

	public void setType(MetadataHolderType type) {
		this.type = type;
	}
	
}
