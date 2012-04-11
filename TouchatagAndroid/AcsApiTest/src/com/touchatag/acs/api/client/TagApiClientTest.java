package com.touchatag.acs.api.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.acs.api.client.model.ClaimingRule;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.TagPage;
import com.touchatag.acs.api.client.model.TagType;
import com.touchatag.android.correlation.api.v1_2.CorrelationGateway;
import com.touchatag.android.correlation.api.v1_2.command.BaseCommand.Logger;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.GenericTagType;
import com.touchatag.android.correlation.api.v1_2.model.ReaderId;
import com.touchatag.android.correlation.api.v1_2.model.TagEvent;
import com.touchatag.android.correlation.api.v1_2.model.TagEventType;
import com.touchatag.android.correlation.api.v1_2.model.TagId;
import com.touchatag.android.correlation.api.v1_2.model.TagInfo;

public class TagApiClientTest {

	private static final String TEST_TAG_IDENTIFIER = "0x11223344";

	private static final String USERNAME = "coreteam";
	private static final String PASSWORD = "X%8ileDE#1";
	private static final TestServer TEST_SERVER = new TestServer();
	// Coreteam identity id
	private static final String OWNERID = "d2e98175-18e4-44cd-917f-b9d866d9c877";
	private static TestServer SERVER = new TestServer();
	private static TagApiClient CLIENT;
	private static CorrelationGateway GATEWAY;

	@BeforeClass
	public static void setUpClass() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CLIENT = new TagApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};
		GATEWAY = new CorrelationGateway(USERNAME, PASSWORD, TEST_SERVER) {

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
	public void testGetTagPage() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		TagPage page = CLIENT.getPage(1, 25);

		Assert.assertNotNull(page);
		Assert.assertEquals(1, page.getPage());
		Assert.assertEquals(25, page.getPageSize());
	}

	@Test
	public void testAcquire() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException, InterruptedException {
		doTouch(1000);
		Thread.sleep(500);
		Tag tag = CLIENT.acquire(OWNERID, OWNERID, "coreteamclientname", 5000);
		
		Assert.assertNotNull(tag);
		Assert.assertEquals(OWNERID, tag.getOwnerId());
		Assert.assertEquals(TEST_TAG_IDENTIFIER, tag.getIdentifier());
	}
	
	@Test
	public void testCancelAcquire() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		doCancelAcquireAtIdentity(1000, OWNERID);
		try {
			CLIENT.acquire(OWNERID, OWNERID, 5000);
			Assert.fail();
		} catch(AcsApiException e){
		}
		
	}
	
	@Test
	public void testGenerateQrTag() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		Tag tag = CLIENT.generateQRTag();
		Assert.assertNotNull(tag);
		Assert.assertEquals(OWNERID, tag.getOwnerId());
		Assert.assertEquals(TagType.QR, tag.getType());
	}
	
	@Test
	public void testEnableDisable() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		Tag tag = CLIENT.generateQRTag();
		Assert.assertFalse(tag.isDisabled());
		CLIENT.disable(tag.getHash());
		tag = CLIENT.getByHash(tag.getHash());
		Assert.assertTrue(tag.isDisabled());
		CLIENT.enable(tag.getHash());
		tag = CLIENT.getByHash(tag.getHash());
		Assert.assertFalse(tag.isDisabled());
	}
	
	@Test
	public void testSetClaimingRule() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		Tag tag = CLIENT.generateQRTag();
		Assert.assertEquals(ClaimingRule.UNLOCKED, tag.getClaimingRule());
		ClaimingRule rule = CLIENT.setClaimingRule(tag.getHash(), ClaimingRule.LOCKED);
		Assert.assertEquals(ClaimingRule.LOCKED, rule);
	}
	
	@Test
	public void testRelinquish() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		Tag tag = CLIENT.generateQRTag();
		CLIENT.relinquish(tag.getHash());
		
		try {
			CLIENT.getByHash(tag.getHash());
			Assert.fail();
		} catch(AcsApiException e){
		}
	}

	private void doCancelAcquireAtIdentity(final long delay, final String atIdentityId){
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Cancelling acquire after delay of " + delay + " msecs");
					TagApiClient client = new TagApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

						@Override
						protected void log(String message) {
							System.out.println(message);
						}
						
					};
					client.cancelAcquireAt(atIdentityId);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}
	
	private void doTouch(final long delay) {
		final TagEvent tagEvent = new TagEvent();
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setClientId(new ClientId("0x123455", "coreteamclientname"));
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
