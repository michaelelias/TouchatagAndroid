package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.MetadataItem;

public class MetadataItemTest {

	private static final String SAMPLE_XML = "metadataItemSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		MetadataItem item = TestUtils.fromXml(xml, MetadataItem.class);
		Assert.assertNotNull(item);
	}

	@Test
	public void testSerialization() throws Exception {
		MetadataItem item = new MetadataItem();
		item.setId("test-id");
		item.setValue("value");
		item.setType("any");
		item.setOwnerId("ownerId");

		String xml = TestUtils.toXml(item);
		System.out.println(xml);
	}

}
