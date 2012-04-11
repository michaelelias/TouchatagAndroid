package com.touchatag.android.correlation.api.v1_2.model;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.android.correlation.api.v1_2.AdapterUtils;
import com.touchatag.android.correlation.api.v1_2.model.SoapEnvelope;

public class TagEventResponseTest {

	@Test
	public void testDeserialization() throws IOException{
		String xml = IOUtils.toString(TagEventResponseTest.class.getResourceAsStream("tagEventResponseSample.xml"));
		System.out.println(xml);
		
		SoapEnvelope envelope = AdapterUtils.fromXml(xml, SoapEnvelope.class);
		Assert.assertNotNull(envelope);
		Assert.assertNotNull(envelope.body);
		Assert.assertNotNull(envelope.body.getTagEventResponse());
		Assert.assertNull(envelope.body.getPingEvent());
		Assert.assertNull(envelope.body.getTagEvent());
		Assert.assertNull(envelope.body.getFault());
		Assert.assertNotNull(envelope.body.getTagEventResponse().getSystemMessage());
		
	}
	
	@Test
	public void testDeserializationLegacyClientAction() throws IOException{
		String xml = IOUtils.toString(TagEventResponseTest.class.getResourceAsStream("tagEventResponseWithLegacyApplicationResponseSample.xml"));
		System.out.println(xml);
		
		SoapEnvelope envelope = AdapterUtils.fromXml(xml, SoapEnvelope.class);
		Assert.assertNotNull(envelope);
		Assert.assertNotNull(envelope.body);
		Assert.assertNotNull(envelope.body.getTagEventResponse());
		Assert.assertNull(envelope.body.getPingEvent());
		Assert.assertNull(envelope.body.getTagEvent());
		Assert.assertNull(envelope.body.getFault());
		Assert.assertNull(envelope.body.getTagEventResponse().getSystemMessage());
		Assert.assertEquals(1, envelope.body.getTagEventResponse().getApplicationResponses().size());
		Assert.assertEquals("urn:com.touchatag:legacy-client-action", envelope.body.getTagEventResponse().getApplicationResponses().get(0).getIdentifier());
		Assert.assertEquals("tikitag.standard.url", envelope.body.getTagEventResponse().getApplicationResponses().get(0).getClientAction().getContainer().getName());
		Assert.assertEquals("v1.0", envelope.body.getTagEventResponse().getApplicationResponses().get(0).getClientAction().getContainer().getContainer().getName());
		Assert.assertEquals("url", envelope.body.getTagEventResponse().getApplicationResponses().get(0).getClientAction().getContainer().getContainer().getAttributes().get(0).getName());
		Assert.assertEquals("http://www.google.com", envelope.body.getTagEventResponse().getApplicationResponses().get(0).getClientAction().getContainer().getContainer().getAttributes().get(0).getString());
		
	}
	
	
	
}
