package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name="ns1:metadataAssociation")
@NamespaceList({
	@Namespace(prefix="ns1", reference="http://acs.touchatag.com/schema/metadataAssociation-1.0"),
	@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/metadataHolder-1.0")
})
public class MetadataAssociation {

	@Element
	private MetadataHolder metadataHolder;
	
	@Element
	private String metadataItemId;

	public MetadataHolder getMetadataHolder() {
		return metadataHolder;
	}

	public void setMetadataHolder(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	public String getMetadataItemId() {
		return metadataItemId;
	}

	public void setMetadataItemId(String metadataItemId) {
		this.metadataItemId = metadataItemId;
	}
	
}
