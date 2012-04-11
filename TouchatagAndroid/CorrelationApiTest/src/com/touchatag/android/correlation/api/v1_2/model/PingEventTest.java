package com.touchatag.android.correlation.api.v1_2.model;

import org.junit.Test;

import com.touchatag.android.correlation.api.v1_2.AdapterUtils;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.PingEvent;

public class PingEventTest {

	@Test
	public void testAdapter(){
		
		PingEvent pingEvent = new PingEvent(new ClientId("clientid"));
		String xml = AdapterUtils.toXml(pingEvent);
		System.out.println(xml);
		
	}
	
	
}
