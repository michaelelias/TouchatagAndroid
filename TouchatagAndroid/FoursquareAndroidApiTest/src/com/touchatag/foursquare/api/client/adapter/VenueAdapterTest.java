package com.touchatag.foursquare.api.client.adapter;

import junit.framework.Assert;

import org.junit.Test;

import com.touchatag.foursquare.api.client.model.Venue;

public class VenueAdapterTest {

	@Test
	public void test(){
		String json = AdapterTestUtils.readFile("venue.json");
		Venue venue = VenueAdapter.fromJSON(json);
		
		Assert.assertNotNull(venue);
		
		Assert.assertEquals("4b70744df964a5206f1b2de3", venue.getId());
		Assert.assertEquals("My Smoking Spot", venue.getName());
		Assert.assertEquals("Brightwater Ave", venue.getAddress());
		Assert.assertEquals("Brooklyn", venue.getCity());
		Assert.assertEquals("NY", venue.getState());
		Assert.assertEquals("11235", venue.getPostalCode());
//		Assert.assertEquals("4b70744df964a5206f1b2de3", venue.get);
//		Assert.assertEquals("4b70744df964a5206f1b2de3", venue.getId());
	}
}
