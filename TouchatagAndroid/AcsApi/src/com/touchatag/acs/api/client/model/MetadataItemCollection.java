package com.touchatag.acs.api.client.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name="bulkcollection", strict=true)
@NamespaceList({
	@Namespace(prefix="ns1", reference="http://acs.touchatag.com/schema/bulkcollection-1.0"),
	@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/metadataItem-1.0"),
	@Namespace(prefix="ns3", reference="http://acs.touchatag.com/schema/metadataAssociation-1.0"),
	@Namespace(prefix="ns4", reference="http://acs.touchatag.com/schema/metadataHolder-1.0"),
	@Namespace(prefix="ns5", reference="http://acs.touchatag.com/schema/metadataLabel-1.0")
})
public class MetadataItemCollection {

	@ElementList(inline=true, entry="ns2:metadataItem")
	List<MetadataItem> items = new ArrayList<MetadataItem>();

	public List<MetadataItem> getItems() {
		return items;
	}
	
}
