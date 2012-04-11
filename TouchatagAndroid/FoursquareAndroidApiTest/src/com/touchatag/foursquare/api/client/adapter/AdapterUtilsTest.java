package com.touchatag.foursquare.api.client.adapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.touchatag.foursquare.api.client.adapter.AdapterUtils.Mapping;
import com.touchatag.foursquare.api.client.model.Venue;

public class AdapterUtilsTest {
	
	@Test
	public void testMapping() throws JSONException{
		Venue venue = new Venue();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("distance", "123");
		
		Mapping<Venue, JSONObject> target = AdapterUtils.mapping(venue, jsonObject);
		
		target.map("distance", "distance", false);
	}
	
}
