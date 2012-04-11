package com.touchatag.acs.api.client;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.android.correlation.api.v1_2.CorrelationGateway;
import com.touchatag.android.correlation.api.v1_2.command.BaseCommand.Logger;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.GenericTagType;
import com.touchatag.android.correlation.api.v1_2.model.ReaderId;
import com.touchatag.android.correlation.api.v1_2.model.TagEvent;
import com.touchatag.android.correlation.api.v1_2.model.TagEventType;
import com.touchatag.android.correlation.api.v1_2.model.TagId;
import com.touchatag.android.correlation.api.v1_2.model.TagInfo;

public class CorrelationDefinitionApiClientTest {

	private static TestServer SERVER = new TestServer();
	private static CorrelationDefinitionApiClient CLIENT;
	private static final String TEST_TAG_IDENTIFIER = "0x11223344";
	private static CorrelationGateway GATEWAY;
	
	private static TagApiClient TAG_API_CLIENT;
	
	// Coreteam identity id
	private static final String OWNERID = "3e0d370e-5f66-4c05-937d-92a6a01348e9";
	private static final String USERNAME = "michael";
	private static final String PASSWORD = "michael";
	

	@BeforeClass
	public static void setUpClass() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CLIENT = new CorrelationDefinitionApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};
		
		TAG_API_CLIENT = new TagApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};

		GATEWAY = new CorrelationGateway(USERNAME, PASSWORD, SERVER) {

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
	public void testGet() throws IOException, AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CorrelationDefinition corrDef = CLIENT.get();

		Assert.assertNotNull(corrDef);
		Assert.assertNotNull(corrDef.getOwnerId());
		Assert.assertEquals(OWNERID, corrDef.getOwnerId());
		
	}

	@Test
	public void testUpdate() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		doTouch(1000);
		
		Tag tag = TAG_API_CLIENT.acquire(OWNERID, OWNERID, 3000);
		
		Assert.assertNotNull(tag);
		
		CorrelationDefinition corrDef = CLIENT.get();
		
		corrDef.associateTagToCommand(tag.getHash(), "somecommand");
		
		CLIENT.update(corrDef);
		
		corrDef = CLIENT.get();
	}

	private void doTouch(final long delay) {
		final TagEvent tagEvent = new TagEvent();
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setClientId(new ClientId("0x123455"));
		tagEvent.setReaderId(new ReaderId("0x9999", "iwe97823djo2djo2"));
		tagEvent.setActionTag(new TagInfo(new TagId(TEST_TAG_IDENTIFIER, GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT)));

		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Performing tag event after delay of " + delay + " msecs");
					GATEWAY.handleTagEvent(tagEvent);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}, delay, TimeUnit.MILLISECONDS);

	}

}
