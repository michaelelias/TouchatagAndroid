package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference="http://acs.touchatag.com/schema/metadataLabel-1.0")
public class MetadataLabel {

	@Attribute(required=false)
	private String id;

	@Attribute(required = true)
	private String name;

	@Attribute(required=false)
	private String normalizedName;

	@Attribute(required=true)
	private String ownerId;

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

	public String getNormalizedName() {
		return normalizedName;
	}

	public void setNormalizedName(String normalizedName) {
		this.normalizedName = normalizedName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

}
