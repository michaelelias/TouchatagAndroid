package com.touchatag.foursquare.api.client;

import junit.framework.Assert;

import org.junit.Test;

import com.touchatag.foursquare.api.client.adapter.AdapterTestUtils;
import com.touchatag.foursquare.api.client.model.Venue;

import flexjson.JSONDeserializer;

public class FlexJsonTest {

	@Test
	public void testVenueDeserialization(){
		String json = AdapterTestUtils.readFile("venue.json");
		
		Venue venue = new JSONDeserializer<Venue>().deserialize(json);
		
		Assert.assertNotNull(venue);
	}
	
}
