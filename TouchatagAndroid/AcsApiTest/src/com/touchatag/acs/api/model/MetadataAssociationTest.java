package com.touchatag.acs.api.model;

import org.junit.Test;

import com.touchatag.acs.api.client.model.MetadataAssociation;
import com.touchatag.acs.api.client.model.MetadataHolder;
import com.touchatag.acs.api.client.model.MetadataHolderType;

public class MetadataAssociationTest {

	private static final String SAMPLE_XML = "metadataItemAssociation.xml";

	@Test
	public void testDeserialization() throws Exception {
		// InputStream is =
		// MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		// String xml = IOUtils.toString(is);
		// System.out.println(xml);
		//
		// MetadataItem item = TestUtils.fromXml(xml, MetadataItem.class);
		// Assert.assertNotNull(item);
	}

	@Test
	public void testSerialization() throws Exception {
		MetadataHolder holder = new MetadataHolder();
		holder.setType(MetadataHolderType.USER);
		holder.setReference("ownerid");

		MetadataAssociation asso = new MetadataAssociation();
		asso.setMetadataHolder(holder);
		asso.setMetadataItemId("item-id");
		String xml = TestUtils.toXml(asso);
		System.out.println(xml);
	}
}
