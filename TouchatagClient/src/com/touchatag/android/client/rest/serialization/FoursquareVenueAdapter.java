package com.touchatag.android.client.rest.serialization;

import org.json.JSONException;
import org.json.JSONObject;

import com.touchatag.android.client.rest.model.FoursquareVenue;

public class FoursquareVenueAdapter {

	public FoursquareVenue fromJSON(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			jsonObject = jsonObject.getJSONObject("response").getJSONObject("venue");
			return fromJSON(jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FoursquareVenue fromJSON(JSONObject jsonObject) {
		FoursquareVenue venue = new FoursquareVenue();

		try {
			venue.setId(jsonObject.getString("id"));
			venue.setName(getString(jsonObject, "name"));
			
			JSONObject jsonLocation = jsonObject.getJSONObject("location");

			venue.setDistance(getString(jsonLocation, "distance"));
			venue.setAddress(getString(jsonLocation, "address"));
			venue.setPostalCode(getString(jsonLocation, "postalCode"));
			venue.setCity(getString(jsonLocation, "city"));
			venue.setState(getString(jsonLocation, "state"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return venue;
	}
	
	private String getString(JSONObject jsonObject, String key) throws JSONException{
		if(jsonObject.has(key)){
			return jsonObject.getString(key);
		}
		return null;
	}

}
