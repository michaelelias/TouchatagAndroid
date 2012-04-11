package com.touchatag.foursquare.api.client.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.touchatag.foursquare.api.client.model.Venue;

public class VenueListAdapter {

	public static List<Venue> fromJSON(String jsonVenues) {
		List<Venue> list = new ArrayList<Venue>();
		try {
			JSONObject jsonObject = new JSONObject(jsonVenues);
			JSONArray venues = jsonObject.getJSONObject("response").getJSONArray("venues");
			for (int i = 0; i < venues.length(); i++) {
				Venue venue = VenueAdapter.fromJSON(venues.getJSONObject(i));
				if (venue != null) {
					list.add(venue);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

}
