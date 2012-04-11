package com.touchatag.android.correlation.api.v1_2.model;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.android.correlation.api.v1_2.AdapterUtils;
import com.touchatag.android.correlation.api.v1_2.model.SoapEnvelope;

public class SoapFaultTest {

	@Test
	public void testDeserialization() throws IOException{
		String xml = IOUtils.toString(SoapFaultTest.class.getResourceAsStream("soapfaultSample.xml"));
		System.out.println(xml);
		
		SoapEnvelope envelope = AdapterUtils.fromXml(xml, SoapEnvelope.class);
		Assert.assertNotNull(envelope);
		Assert.assertNotNull(envelope.body);
		Assert.assertNotNull(envelope.body.getFault());
		Assert.assertNull(envelope.body.getPingEvent());
		Assert.assertNull(envelope.body.getTagEvent());
		Assert.assertEquals("env:Client", envelope.body.getFault().code);
		
	}
	
}
