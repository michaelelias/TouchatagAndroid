package com.touchatag.beta.activity.template;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.maps.GeoPoint;
import com.touchatag.beta.R;
import com.touchatag.beta.util.GeoUtils;
import com.touchatag.beta.util.NotificationUtils;
import com.touchatag.foursquare.api.client.FoursquareRestClient;
import com.touchatag.foursquare.api.client.model.Venue;

public class FoursquareVenuePickerActivity extends Activity {

	private static final int REQ_CODE_PICK_LOCATION = 1;

	public static final String EXTRA_VENUE_ID = "venue.id";
	public static final String EXTRA_VENUE_NAME = "venue.name";
	public static final String EXTRA_VENUE_ADDRESS = "venue.address";

	private ListView listVenues;
	private FoursquareVenueListAdapter listAdapter;
	private List<Venue> venues = new ArrayList<Venue>();
	private EditText txtQuery;

	private boolean searchNearCurrentLocation = true;
	private GeoPoint searchNearGeoPoint;
	private String address;

	private Button btnLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Pick a Foursquare Venue");
		setContentView(R.layout.foursquare_venue_picker);
		listVenues = (ListView) findViewById(R.id.list_4sq_venues);

		listVenues.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Venue venue = venues.get(position);
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

		btnLocation = (Button) findViewById(R.id.btn_4sq_location);
		btnLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickLocation();
			}
		});
		selectCurrentLocation();

		txtQuery = (EditText) findViewById(R.id.txt_4sq_search);

		ImageButton btnMyLocation = (ImageButton) findViewById(R.id.btn_4sq_mylocation);
		btnMyLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectCurrentLocation();
			}
		});

		ImageButton btnSearch = (ImageButton) findViewById(R.id.btn_4sq_search);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchVenues(txtQuery.getText().toString());
			}
		});
		// searchNearbyVenues();
	}

	private void selectCurrentLocation() {
		searchNearCurrentLocation = true;
		btnLocation.setText("My Location");
	}

	private void pickLocation() {
		startActivityForResult(PickLocationActivity.getPickLocationAtLastKnownLocationIntent(this), REQ_CODE_PICK_LOCATION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_PICK_LOCATION && resultCode == Activity.RESULT_OK) {
			int latitude = data.getIntExtra(PickLocationActivity.EXTRA_LATITUDE, 0);
			int longitude = data.getIntExtra(PickLocationActivity.EXTRA_LONGITUDE, 0);
			address = data.getStringExtra(PickLocationActivity.EXTRA_ADDRESS);
			searchNearGeoPoint = new GeoPoint(latitude, longitude);
			searchNearCurrentLocation = false;
			btnLocation.setText(address);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void searchVenues(String query) {
		String latitude = null;
		String longitude = null;
		if (searchNearCurrentLocation) {
			Location location = getLastKnownLocation();
			if (location == null) {
				NotificationUtils.showFeedbackMessage(this, "Failed to get your last known location. Try again.");
				return;
			}
			latitude = ((Double) location.getLatitude()).toString();
			longitude = ((Double) location.getLongitude()).toString();
		} else {
			;
			latitude = GeoUtils.toDouble(searchNearGeoPoint.getLatitudeE6()).toString();
			longitude = GeoUtils.toDouble(searchNearGeoPoint.getLongitudeE6()).toString();
		}
		SearchVenuesAsyncTask task = new SearchVenuesAsyncTask();
		task.execute(latitude, longitude, query);
	}

	private Location getLastKnownLocation() {
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// criteria.setAltitudeRequired(false);
		// criteria.setBearingRequired(false);
		// criteria.setCostAllowed(true);
		// criteria.setPowerRequirement(Criteria.POWER_LOW);
		// String provider = locationManager.getBestProvider(criteria, true);
		// LocationProvider locationProvider =
		// locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
			Venue venue = venues.get(position);

			TextView lblName = (TextView) convertView.findViewById(R.id.lbl_4sq_venue_name);
			lblName.setText(venue.getName());
			TextView lblAddress = (TextView) convertView.findViewById(R.id.lbl_4sq_venue_address);
			lblAddress.setText(venue.getAddressAsFormattedString());
			return convertView;
		}
	}

	private class SearchVenuesAsyncTask extends AsyncTask<String, Void, List<Venue>> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(FoursquareVenuePickerActivity.this, null, "Searching Venues...");
		}

		@Override
		protected List<Venue> doInBackground(String... params) {
			String latitude = params[0];
			String longitude = params[1];
			String query = params[2];

			FoursquareRestClient client = new FoursquareRestClient(){

				@Override
				public void log(String message) {
					Log.i("FoursquareRestClient", message);
				}
				
			};
			return client.searchVenues(latitude, longitude, query);
		}

		@Override
		protected void onPostExecute(List<Venue> result) {
			super.onPostExecute(result);
			venues = result;
			progressDialog.dismiss();
			listAdapter.notifyDataSetChanged();
			if (venues.size() == 0) {
				NotificationUtils.showFeedbackMessage(FoursquareVenuePickerActivity.this, "No venues found, try again.");
			}
		}
	}
}
