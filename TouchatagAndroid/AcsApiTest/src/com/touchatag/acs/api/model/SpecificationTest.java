package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.specification.Specification;

public class SpecificationTest {

	private static final String SAMPLE_XML = "specificationSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		Specification spec = TestUtils.fromXml(xml, Specification.class);
		Assert.assertNotNull(spec);
		
		Assert.assertEquals(1, spec.application.commands.size());
		Assert.assertEquals("default", spec.application.commands.get(0).name);
		Assert.assertEquals("default", spec.application.commands.get(0).id);
		
		Assert.assertEquals("urn:touchatag:block:web-link-app", spec.blueprint.superBlock.ref);
		Assert.assertEquals("*", spec.blueprint.superBlock.id);
		Assert.assertEquals(1, spec.blueprint.superBlock.properties.size());
		Assert.assertEquals("http://www.touchatag.com/developer", spec.blueprint.superBlock.properties.get(0).getUri());
		
	}

	@Test
	public void testSerialization() throws Exception {
		// MetadataItem item = new MetadataItem();
		// item.setId("test-id");
		// item.setValue("value");
		// item.setType("any");
		// item.setOwnerId("ownerId");
		//
		// String xml = TestUtils.toXml(item);
		// System.out.println(xml);
	}
}
