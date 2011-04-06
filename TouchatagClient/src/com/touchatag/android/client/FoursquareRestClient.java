package com.touchatag.android.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.touchatag.android.client.rest.model.FoursquareVenue;
import com.touchatag.android.client.rest.serialization.FoursquareVenueAdapter;
import com.touchatag.android.client.rest.serialization.FoursquareVenueListAdapter;

public class FoursquareRestClient {

	private static final String BASE_URL = "https://api.foursquare.com/v2/";
	private static final String CLIENT_ID = "FGBJ11X3B4JNIMRA1HBO3OYNN3WBIFSYUAPW5GVZAVQTDT3X";
	private static final String CLIENT_SECRET = "SQVEPFJB1ZRTLYASBJVUHTWX4M00OSYXKS3SQ05DHGWR03L0";
	private HttpClient httpClient;
	
	public FoursquareRestClient(){
		httpClient = new DefaultHttpClient();
	}
	
	public FoursquareVenue findVenueById(String venueId){
		String url = BASE_URL + "venues/" + venueId + "?";
		HttpGet httpGet = new HttpGet(appendOAuthCredentials(url));
		
		try {
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);
			
			if(statusCode == 200){
				return new FoursquareVenueAdapter().fromJSON(responseBody);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	public List<FoursquareVenue> searchVenues(String coordinates, String query){
		query = URLEncoder.encode(query);
		String url = BASE_URL + "venues/search?ll=" + coordinates + "&query=" + query + "&";
		HttpGet httpGet = new HttpGet(appendOAuthCredentials(url));
		try {
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = getStatusCode(response);
			String responseBody = getResonseBody(response);
			
			if(statusCode == 200){
				return new FoursquareVenueListAdapter().fromJSON(responseBody);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new ArrayList<FoursquareVenue>();
	}
	
	private String appendOAuthCredentials(String url){
		return url += "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;
	}
	
	private int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	private String getResonseBody(HttpResponse response) {
		try {
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(response.getEntity());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bufferedHttpEntity.writeTo(baos);
			return baos.toString("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
