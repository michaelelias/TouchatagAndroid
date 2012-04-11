package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.Application;

public class ApplicationTest {

	private static final String SAMPLE_XML = "applicationSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		Application app = TestUtils.fromXml(xml, Application.class);
		Assert.assertNotNull(app);
		Assert.assertNotNull(app.getSpecification());
	}

	@Test
	public void testSerialization() throws Exception {
		InputStream is = MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);

		Application app = TestUtils.fromXml(xml, Application.class);
		
		xml = TestUtils.toXml(app);
		System.out.println(xml);
	}
	
}
