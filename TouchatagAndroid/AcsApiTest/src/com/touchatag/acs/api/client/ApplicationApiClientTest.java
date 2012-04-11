package com.touchatag.acs.api.client;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.model.ApplicationTest;
import com.touchatag.android.correlation.api.v1_2.AdapterUtils;

public class ApplicationApiClientTest {

	private static TestServer SERVER = new TestServer();
	private static ApplicationApiClient CLIENT;
	
	@BeforeClass
	public static void setUpClass() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CLIENT = new ApplicationApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};
	}
	
	@Test
	public void testCreate() throws IOException, AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		InputStream is = ApplicationTest.class.getResourceAsStream("applicationSample.xml");
		String xml = IOUtils.toString(is);
		Application app = AdapterUtils.fromXml(xml, Application.class);
		Application createdApp = CLIENT.create(app);
		
		Assert.assertNotNull(createdApp);
		Assert.assertNotNull(createdApp.getId());
	}
	
	
}
