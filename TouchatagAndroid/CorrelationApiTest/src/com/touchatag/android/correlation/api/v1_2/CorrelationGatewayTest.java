package com.touchatag.android.correlation.api.v1_2;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.android.correlation.api.v1_2.CorrelationGateway;
import com.touchatag.android.correlation.api.v1_2.command.InvalidCredentialsException;
import com.touchatag.android.correlation.api.v1_2.command.SoapFaultException;
import com.touchatag.android.correlation.api.v1_2.command.BaseCommand.Logger;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.GenericTagType;
import com.touchatag.android.correlation.api.v1_2.model.PingEvent;
import com.touchatag.android.correlation.api.v1_2.model.ReaderId;
import com.touchatag.android.correlation.api.v1_2.model.TagEvent;
import com.touchatag.android.correlation.api.v1_2.model.TagEventType;
import com.touchatag.android.correlation.api.v1_2.model.TagId;
import com.touchatag.android.correlation.api.v1_2.model.TagInfo;

public class CorrelationGatewayTest {

	private static final String USERNAME = "coreteam";
	private static final String PASSWORD = "X%8ileDE#1";
	private static final TestServer TEST_SERVER = new TestServer();
	private static CorrelationGateway GATEWAY;
	
	@BeforeClass
	public static void setUpBefore(){
		GATEWAY = new CorrelationGateway(USERNAME, PASSWORD, TEST_SERVER){

			@Override
			public Logger createLogger() {
				return new Logger() {

					@Override
					public void log(String message) {
						System.out.println(message);
					}
				};
			}
			
		};
	}
	
	@Test
	public void testPingEventWithCorrectCredentials() throws InvalidCredentialsException, SoapFaultException{
		PingEvent pingEvent = new PingEvent(new ClientId("clientid"));
		GATEWAY.ping(pingEvent);
	}
	
	@Test
	public void testPingEventWithIncorrectCredentials() throws SoapFaultException{
		PingEvent pingEvent = new PingEvent(new ClientId("clientid"));

		CorrelationGateway gateway = new CorrelationGateway("dummy", "passw", TEST_SERVER){

			@Override
			public Logger createLogger() {
				return new Logger() {

					@Override
					public void log(String message) {
						System.out.println(message);
					}
				};
			}
			
		};
		try {
			gateway.ping(pingEvent);
			Assert.fail();
		} catch(InvalidCredentialsException e){
			
		}
	}
	
	@Test
	public void testTagEvent() throws InvalidCredentialsException, SoapFaultException{
		TagEvent tagEvent = new TagEvent();
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setClientId(new ClientId("0x123455"));
		tagEvent.setReaderId(new ReaderId("0x9999", "iwe97823djo2djo2"));
		tagEvent.setActionTag(new TagInfo(new TagId("0x04E5FC193E2580", GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT)));
		
		GATEWAY.handleTagEvent(tagEvent);
	}
}
