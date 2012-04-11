package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.AcsIdentity;

public class AcsIdentityTest {

	private static final String SAMPLE_XML = "acsIdentitySample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = AcsIdentityTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		AcsIdentity identity = TestUtils.fromXml(xml, AcsIdentity.class);
		Assert.assertNotNull(identity);
		Assert.assertEquals("d96cc489-1d39-43e4-8a9d-9408d892d2cc", identity.getIdentityId());
		Assert.assertNotNull(identity.getRoles());
		Assert.assertEquals(3, identity.getRoles().size());
	}

	@Test
	public void testSerialization() throws Exception {
		InputStream is = AcsIdentityTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);

		AcsIdentity identity = TestUtils.fromXml(xml, AcsIdentity.class);
		
		xml = TestUtils.toXml(identity);
		System.out.println(xml);
	}
	
}
