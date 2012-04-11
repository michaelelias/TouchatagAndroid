package com.touchatag.beta.client.soap.serialization;

import com.touchatag.beta.client.soap.model.request.PingEvent;

public class PingEventSerializer {

	private static String PINGEVENT = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:ping xmlns:ns3=\"http://www.touchatag.com/acs/api/correlation-1.1\" xmlns:ns2=\"http://www.touchatag.com/acs/api/correlation-1.2\"><clientId><id>${clientId}</id><name>${clientName}</name></clientId></ns2:ping></S:Body></S:Envelope>";
	
	private static String PROP_CLIENT_ID = "clientId";
	private static String PROP_CLIENT_NAME = "clientName";
	
	public static String serialize(PingEvent pingEvent){
		String serializedPingEvent = PINGEVENT;
		
		serializedPingEvent = replaceProperty(serializedPingEvent, PROP_CLIENT_ID, pingEvent.getClientId().getId());
		serializedPingEvent = replaceProperty(serializedPingEvent, PROP_CLIENT_NAME, pingEvent.getClientId().getName());
	
		return serializedPingEvent;
	}
	
	
	private static String replaceProperty(String source, String prop, String value){
		if(value == null){
			value = "";
		}
		return source.replace("${" + prop + "}", value);
	}
}
