package com.touchatag.acs.api.model;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.touchatag.acs.api.client.model.ruleset.Association;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;

public class CorrelationDefinitionTest {

	private static final String SAMPLE_XML = "correlationDefinitionSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		 InputStream is = CorrelationDefinitionTest.class.getResourceAsStream(SAMPLE_XML);
		 String xml = IOUtils.toString(is);
		 System.out.println(xml);
		
		 CorrelationDefinition corrDef = TestUtils.fromXml(xml, CorrelationDefinition.class);
		 Assert.assertNotNull(corrDef);
		 Assert.assertNotNull(corrDef.getAssociations());
		 Assert.assertEquals(1, corrDef.getAssociations().size());
		 Assert.assertEquals("sometagid", corrDef.getAssociations().get(0).getTagId());
		 Assert.assertEquals("command", corrDef.getAssociations().get(0).getCommand());
	}

	@Test
	public void testSerialization() throws Exception {
		CorrelationDefinition corrDef = new CorrelationDefinition();
		
		Association asso = new Association();
		asso.setTagId("sometagid");
		asso.setCommand("command");
		corrDef.getAssociations().add(asso);

		String xml = TestUtils.toXml(corrDef);
		System.out.println(xml);
	}
	
}
