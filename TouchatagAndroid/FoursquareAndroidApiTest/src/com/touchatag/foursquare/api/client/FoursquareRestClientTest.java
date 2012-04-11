package com.touchatag.foursquare.api.client;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.foursquare.api.client.model.Venue;

public class FoursquareRestClientTest {
	
	private static String TOKEN = "DSYJU45T23VKRUBU0MI1MR4MEOT4LWEXF2RUO53Q4CQ1TGZM";

	private static FoursquareRestClient CLIENT;
	
	@BeforeClass
	public static void setUpClass(){
		CLIENT = new FoursquareRestClient(){

			@Override
			public void log(String message) {
				System.out.println(message);
			}
			
		};
		CLIENT.setToken(TOKEN);
	}
	
	@Test
	public void testSearchVenues(){
		String latitude = "40.47";
		String longitude = "73.58";
		String query = "";
		
		List<Venue> venues = CLIENT.searchVenues(latitude, longitude, query);
		
		Assert.assertNotNull(venues);
		Assert.assertTrue(venues.size() > 0);
	}
	
	@Test
	public void testGetVenue(){
		Venue venue = CLIENT.findVenueById("4b70744df964a5206f1b2de3");
		Assert.assertNotNull(venue);
	}
	
	@Test
	public void testGetActingUser(){
		CLIENT.getActingUser();
	}
	
	@Test
	public void testAddCheckin(){
		String venueId = "4b70744df964a5206f1b2de3";
		String shout = "helloworld";
		String broadcast = "private";
		String latitude = "40.58583333333333";
		String longitude = "73.94444444444444";
		
		CLIENT.addCheckin(venueId, shout, broadcast, latitude, longitude);
	}
	
}
