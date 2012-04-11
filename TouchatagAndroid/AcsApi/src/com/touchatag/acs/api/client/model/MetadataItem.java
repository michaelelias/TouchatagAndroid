package com.touchatag.acs.api.client.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root
@NamespaceList({
	@Namespace(reference="http://acs.touchatag.com/schema/metadataItem-1.0"),
	@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/metadataLabel-1.0")
})
public class MetadataItem {

	@Attribute(required=false)
	private String id;

	@Attribute
	private String ownerId;

	@Attribute(required = true)
	private String type;

	@Attribute(required=false)
	private String value;

//	@Attribute(required=false)
//	private List<String> metadataLabelRef = new ArrayList<String>();
//
	@ElementList(required=false, inline=true)
	private List<MetadataLabel> labels = new ArrayList<MetadataLabel>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

//	public List<String> getMetadataLabelRef() {
//		return metadataLabelRef;
//	}
//
//	public void setMetadataLabelRef(List<String> metadataLabelRef) {
//		this.metadataLabelRef = metadataLabelRef;
//	}
//
	public List<MetadataLabel> getLabels() {
		return labels;
	}

	public void setMetadataLabel(List<MetadataLabel> labels) {
		this.labels = labels;
	}

}
