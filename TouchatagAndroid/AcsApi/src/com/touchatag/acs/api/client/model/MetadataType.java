package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference = "http://acs.touchatag.com/schema/metadataType-1.0")
public class MetadataType {

	@Attribute(required=false)
	private String description;

	@Attribute(required=false)
	private String formatDescriptionUri;

	@Attribute(required = true)
	private String identifier;

	@Attribute(required=false)
	private String ownerId;

	@Attribute(required=false)
	private MetadataTypeScope scope;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormatDescriptionUri() {
		return formatDescriptionUri;
	}

	public void setFormatDescriptionUri(String formatDescriptionUri) {
		this.formatDescriptionUri = formatDescriptionUri;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public MetadataTypeScope getScope() {
		return scope;
	}

	public void setScope(MetadataTypeScope scope) {
		this.scope = scope;
	}

}
