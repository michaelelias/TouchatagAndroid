package com.touchatag.foursquare.api.client.adapter;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.touchatag.foursquare.api.client.model.Venue;

public class VenuesListAdapterTest {

	@Test
	public void test(){
		String json = AdapterTestUtils.readFile("venues.json");
		
		List<Venue> venues = VenueListAdapter.fromJSON(json);
		
		Assert.assertNotNull(venues);
		Assert.assertTrue(venues.size() > 3);
		
		
	}
	
}
