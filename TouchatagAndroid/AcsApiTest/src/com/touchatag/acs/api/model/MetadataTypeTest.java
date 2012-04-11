package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.MetadataType;
import com.touchatag.acs.api.client.model.MetadataTypeScope;

public class MetadataTypeTest {

private static final String SAMPLE_XML = "metadataTypeSample.xml";
	
	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataLabelTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);
		
		MetadataType type = TestUtils.fromXml(xml, MetadataType.class);
		Assert.assertNotNull(type);
	}
	
	@Test
	public void testSerialization() throws Exception {
		MetadataType type = new MetadataType();
		type.setDescription("description");
		type.setFormatDescriptionUri("urn:touchatag:testtype");
		type.setIdentifier("testtype");
		type.setOwnerId("ownerid");
		type.setScope(MetadataTypeScope.PUBLIC);
		
		String xml = TestUtils.toXml(type);
		System.out.println(xml);
	}
	
}
