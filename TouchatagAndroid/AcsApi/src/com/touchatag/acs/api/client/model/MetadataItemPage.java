package com.touchatag.acs.api.client.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name="ns1:page")
@NamespaceList({
	@Namespace(prefix="ns1", reference="http://acs.touchatag.com/schema/page-1.0"),
	@Namespace(reference="http://acs.touchatag.com/schema/metadataItem-1.0"),
	@Namespace(prefix="ns3", reference="http://acs.touchatag.com/schema/metadataLabel-1.0"),
	@Namespace(prefix="ns4", reference="http://acs.touchatag.com/schema/metadataHolder-1.0"),
	@Namespace(prefix="ns5", reference="http://acs.touchatag.com/schema/metadataAssociation-1.0"),
	@Namespace(prefix="ns6", reference="http://acs.touchatag.com/schema/tag-1.0"),
	@Namespace(prefix="ns7", reference="http://acs.touchatag.com/schema/accessToken-1.0"),
	@Namespace(prefix="ns8", reference="http://acs.touchatag.com/schema/metadataType-1.0"),
	@Namespace(prefix="ns9", reference="http://acs.touchatag.com/schema/ruleset-1.0"),
	@Namespace(prefix="ns10", reference="http://acs.touchatag.com/schema/associations-1.0"),
	@Namespace(prefix="ns11", reference="http://acs.touchatag.com/schema/correlationDefinition-1.0"),
	@Namespace(prefix="ns12", reference="http://acs.touchatag.com/schema/acsIdentity-1.0"),
	@Namespace(prefix="ns13", reference="http://acs.touchatag.com/schema/specification-1.0"),
	@Namespace(prefix="ns14", reference="http://acs.touchatag.com/schema/application-1.0"),
})
public class MetadataItemPage extends BasePage {

	@ElementList(inline=true, required=false)
	private List<MetadataItem> items = new ArrayList<MetadataItem>();
	
	@Override
	public List<MetadataItem> getItems() {
		return items;
	}

}
