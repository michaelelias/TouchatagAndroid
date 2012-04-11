package com.touchatag.foursquare.api.client.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.touchatag.foursquare.api.client.adapter.AdapterUtils.Mapping;
import com.touchatag.foursquare.api.client.model.Venue;

public class VenueAdapter {

	public static Venue fromJSON(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			jsonObject = jsonObject.getJSONObject("response").getJSONObject("venue");
			return fromJSON(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Venue fromJSON(JSONObject jsonObject) {
		Venue venue = new Venue();
		try {
			Mapping<Venue, JSONObject> mapping = AdapterUtils.mapping(venue, jsonObject);
			
			mapping.map("id", "id");
			mapping.map("name", "name");
			
			mapping.setSource(jsonObject.getJSONObject("location"));
			
			mapping.map("distance", "distance", false);
			mapping.map("address", "address", false);
			mapping.map("postalCode", "postalCode", false);
			mapping.map("city", "city", false);
			mapping.map("state", "state", false);
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return venue;
	}
	
	private static String getString(JSONObject jsonObject, String key) throws JSONException{
		if(jsonObject.has(key)){
			return jsonObject.getString(key);
		}
		return null;
	}
	
	

}
