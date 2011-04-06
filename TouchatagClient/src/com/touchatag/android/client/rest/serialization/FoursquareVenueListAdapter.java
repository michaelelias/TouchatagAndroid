package com.touchatag.android.client.rest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.touchatag.android.client.rest.model.FoursquareVenue;

public class FoursquareVenueListAdapter {

	public List<FoursquareVenue> fromJSON(String jsonVenues){
		List<FoursquareVenue> venues = new ArrayList<FoursquareVenue>();
		FoursquareVenueAdapter venueAdapter = new FoursquareVenueAdapter();
		try {
			JSONObject jsonObject = new JSONObject(jsonVenues);
			JSONArray jsoGroups = jsonObject.getJSONObject("response").getJSONArray("groups");
			
			for(int i = 0; i < jsoGroups.length(); i++){
				JSONObject jsonGroup  = jsoGroups.getJSONObject(i);
				JSONArray jsonItems = jsonGroup.getJSONArray("items");
				for(int j = 0; j < jsonItems.length(); j++){
					FoursquareVenue venue = venueAdapter.fromJSON(jsonItems.getJSONObject(j));
					if(venue != null){
						venues.add(venue);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return venues;
	}
	
}
