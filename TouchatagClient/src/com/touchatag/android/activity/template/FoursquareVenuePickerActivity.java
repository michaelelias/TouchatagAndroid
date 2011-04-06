package com.touchatag.android.activity.template;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.client.FoursquareRestClient;
import com.touchatag.android.client.rest.model.FoursquareVenue;
import com.touchatag.android.util.NotificationUtils;

public class FoursquareVenuePickerActivity extends Activity {

	public static final String EXTRA_VENUE_ID = "venue.id";
	public static final String EXTRA_VENUE_NAME = "venue.name";
	public static final String EXTRA_VENUE_ADDRESS = "venue.address";
	
	private ListView listVenues;
	private FoursquareVenueListAdapter listAdapter;
	private List<FoursquareVenue> venues = new ArrayList<FoursquareVenue>();
	private EditText txtQuery;
	private Button btnSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Pick a Foursquare Venue");
		setContentView(R.layout.foursquare_venue_picker);
		listVenues = (ListView) findViewById(R.id.list_4sq_venues);

		listVenues.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 FoursquareVenue venue = venues.get(position);
				 Intent intent = new Intent();
				 intent.putExtra(EXTRA_VENUE_ID, venue.getId());
				 intent.putExtra(EXTRA_VENUE_NAME, venue.getName());
				 intent.putExtra(EXTRA_VENUE_ADDRESS, venue.getAddress());
				 setResult(RESULT_OK, intent);
				finish();
			}

		});

		listAdapter = new FoursquareVenueListAdapter(this);
		listVenues.setAdapter(listAdapter);

		txtQuery = (EditText) findViewById(R.id.txt_4sq_search);

		ImageButton btnSearch = (ImageButton) findViewById(R.id.btn_4sq_search);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchVenues(getLastKnownLocation(), txtQuery.getText().toString());
			}
		});
		searchNearbyVenues();
	}

	private void searchNearbyVenues(){
		searchVenues(getLastKnownLocation(), "");
	}
	
	private void searchVenues(Location location, String query) {
		String latitude = ((Double)location.getLatitude()).toString();
		String longitude = ((Double)location.getLongitude()).toString();
		String llString = latitude + "," + longitude;
		SearchVenuesAsyncTask task = new SearchVenuesAsyncTask();
		task.execute(llString, query);
	}

	private Location getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		return locationManager.getLastKnownLocation(provider);
	}

	private class FoursquareVenueListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public FoursquareVenueListAdapter(Context ctx) {
			layoutInflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return venues.size();
		}

		@Override
		public Object getItem(int position) {
			return venues.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.foursquare_venue_item, null);
			}
			FoursquareVenue venue = venues.get(position);

			TextView lblName = (TextView) convertView.findViewById(R.id.lbl_4sq_venue_name);
			lblName.setText(venue.getName());
			TextView lblAddress = (TextView) convertView.findViewById(R.id.lbl_4sq_venue_address);
			lblAddress.setText(venue.getAddressAsFormattedString());
			return convertView;
		}
	}

	private class SearchVenuesAsyncTask extends AsyncTask<String, Void, List<FoursquareVenue>> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(FoursquareVenuePickerActivity.this, null, "Searching Venues...");
		}

		@Override
		protected List<FoursquareVenue> doInBackground(String... params) {
			String coordinates = params[0];
			String query = params[1];

			FoursquareRestClient client = new FoursquareRestClient();
			return client.searchVenues(coordinates, query);
		}

		@Override
		protected void onPostExecute(List<FoursquareVenue> result) {
			super.onPostExecute(result);
			venues = result;
			progressDialog.dismiss();
			listAdapter.notifyDataSetChanged();
			if(venues.size() == 0){
				NotificationUtils.showFeedbackMessage(FoursquareVenuePickerActivity.this, "No venues found, try again.");
			}
		}
	}
}
