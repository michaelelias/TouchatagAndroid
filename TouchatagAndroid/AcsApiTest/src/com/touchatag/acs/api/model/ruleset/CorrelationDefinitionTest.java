package com.touchatag.acs.api.model.ruleset;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.acs.api.model.TestUtils;

public class CorrelationDefinitionTest {

	private static final String SAMPLE_EMPTY_XML = "correlationDefinitionEmptySample.xml";

	@Test
	public void testDeserializationEmpty() throws Exception {
		InputStream is = CorrelationDefinitionTest.class.getResourceAsStream(SAMPLE_EMPTY_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		CorrelationDefinition corrDef = TestUtils.fromXml(xml, CorrelationDefinition.class);
		Assert.assertNotNull(corrDef);
		Assert.assertNotNull(corrDef.getOwnerId());
	}

	@Test
	public void testSerializationEmpty() throws Exception {
		InputStream is = CorrelationDefinitionTest.class.getResourceAsStream(SAMPLE_EMPTY_XML);
		String xml = IOUtils.toString(is);

		CorrelationDefinition corrDef = TestUtils.fromXml(xml, CorrelationDefinition.class);
		
		xml = TestUtils.toXml(corrDef);
		System.out.println(xml);
	}
	
}
