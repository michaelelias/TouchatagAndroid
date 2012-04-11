package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.MetadataLabel;

public class MetadataLabelTest {

	private static final String SAMPLE_XML = "metadataLabelSample.xml";
	
	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataLabelTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);
		
		MetadataLabel label = TestUtils.fromXml(xml, MetadataLabel.class);
		Assert.assertNotNull(label);
	}
	
	@Test
	public void testSerialization() throws Exception {
		MetadataLabel label = new MetadataLabel();
		label.setId("test-id");
		label.setName("name");
		label.setOwnerId("ownerId");
		
		String xml = TestUtils.toXml(label);
		System.out.println(xml);
	}
}
