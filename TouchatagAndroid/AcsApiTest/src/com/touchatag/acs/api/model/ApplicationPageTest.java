package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.ApplicationPage;

public class ApplicationPageTest {

	private static final String SAMPLE_XML = "applicationPageSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = ApplicationPageTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		ApplicationPage page = TestUtils.fromXml(xml, ApplicationPage.class);
		Assert.assertNotNull(page);
		Assert.assertEquals(1, page.getItems().size());
		//Assert.assertNotNul(1, page.getItems().get(0).getSpecification());
		
	}
	
}
