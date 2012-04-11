package com.touchatag.acs.api.model;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.acs.api.client.model.MetadataItemPage;

public class MetadataItemPageTest {

	private static final String SAMPLE_XML = "metadataItemPageSample.xml";

	@Test
	public void testDeserialization() throws Exception {
		InputStream is = MetadataItemTest.class.getResourceAsStream(SAMPLE_XML);
		String xml = IOUtils.toString(is);
		System.out.println(xml);

		MetadataItemPage page = TestUtils.fromXml(xml, MetadataItemPage.class);
		Assert.assertNotNull(page);
		Assert.assertEquals(1, page.getItems().size());
		Assert.assertEquals(1, page.getItems().get(0).getLabels().size());
		
	}

}
