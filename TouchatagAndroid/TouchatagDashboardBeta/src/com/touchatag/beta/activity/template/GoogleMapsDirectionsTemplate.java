package com.touchatag.beta.activity.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touchatag.acs.api.client.model.specification.Property;
import com.touchatag.acs.api.client.model.specification.Specification;
import com.touchatag.beta.R;
import com.touchatag.beta.client.soap.model.response.AndroidClientActionResponse;
import com.touchatag.beta.util.GeoUtils;
import com.touchatag.beta.util.NotificationUtils;
import com.touchatag.beta.util.Utils;

public class GoogleMapsDirectionsTemplate extends BaseTemplate {

	private static String PARAM_URI = "uri";
	private static String PARAM_START_POINT_LATITUDE = "startlatitude";
	private static String PARAM_START_POINT_LONGITUDE = "startlongitude";
	private static String PARAM_START_POINT_ADDRESS = "startaddress";
	private static String PARAM_END_POINT_LATITUDE = "endlatitude";
	private static String PARAM_END_POINT_LONGITUDE = "endlongitude";
	private static String PARAM_END_POINT_ADDRESS = "endaddress";
	
	private Button btnStartPoint;
	private Button btnEndPoint;

	private boolean requestedStartPoint;
	private boolean requestedEndPoint;

	private Double latitudeStartPoint;
	private Double longitudeStartPoint;
	private String addressStartPoint;
	private Double latitudeEndPoint;
	private Double longitudeEndPoint;
	private String addressEndPoint;
	
	private Location myLocation;

	private LocationType startPointLocationType;
	private LocationType endPointLocationType;

	private enum LocationType {
		MY_LOCATION("My Location", "Your current location."), //
		TAG_LOCATION("Tag Location", "The tag's location."), //
		USER_LOCATION("User Location", "The location of the user that scanned the tag."), //
		PICK_LOCATION("Pick Location...", "Pick a location on a map.");

		private String name;
		private String description;

		private LocationType(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public static List<LocationType> getStartPointLocationTypes() {
			List<LocationType> list = new ArrayList<GoogleMapsDirectionsTemplate.LocationType>();
			list.add(MY_LOCATION);
			//list.add(TAG_LOCATION);
			list.add(PICK_LOCATION);
			return list;
		}
	}

	protected GoogleMapsDirectionsTemplate() {
		super("Google Maps Directions", "Opens the Google Maps app with directions", "com.google.android.apps.maps");
	}

	@Override
	public boolean isCorrectPackageInfo(PackageInfo packageInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRequestCode() {
		return 901;
	}

	@Override
	public Intent getActivityForResultIntent(Activity activity) {
		return new Intent(activity, GoogleMapsDirectionsTemplate.class);
	}

	@Override
	public void processActivityResult(Activity activity, Intent data) {
		int latitude = data.getIntExtra(PickLocationActivity.EXTRA_LATITUDE, -1);
		int longitude = data.getIntExtra(PickLocationActivity.EXTRA_LONGITUDE, -1);
		String address = data.getStringExtra(PickLocationActivity.EXTRA_ADDRESS);

		if (requestedStartPoint) {
			latitudeStartPoint = GeoUtils.toDouble(latitude);
			longitudeStartPoint = GeoUtils.toDouble(longitude);
			addressStartPoint = address;
			myLocation = null;
			btnStartPoint.setText(addressStartPoint);
		} else if (requestedEndPoint) {
			latitudeEndPoint = GeoUtils.toDouble(latitude);
			longitudeEndPoint = GeoUtils.toDouble(longitude);
			addressEndPoint = address;
			btnEndPoint.setText(addressEndPoint);
		}

		requestedStartPoint = false;
		requestedEndPoint = false;
	}

	@Override
	public Specification createSpecification(Activity activity) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://maps.google.com/maps?");
		sb.append("saddr=");
		sb.append(latitudeStartPoint.toString());
		sb.append(",");
		sb.append(longitudeStartPoint.toString());
		sb.append("&daddr=");
		sb.append(latitudeEndPoint.toString());
		sb.append(",");
		sb.append(longitudeEndPoint.toString());
		
		String uri = sb.toString();
		uri = Utils.encodeAmpersant(uri);
		
		Map<String, String> params = new TreeMap<String, String>();
		params.put(PARAM_URI, uri);
		params.put(PARAM_START_POINT_LATITUDE, latitudeStartPoint.toString());
		params.put(PARAM_START_POINT_LONGITUDE, longitudeStartPoint.toString());
		params.put(PARAM_START_POINT_ADDRESS, addressStartPoint);
		
		params.put(PARAM_END_POINT_LATITUDE, latitudeEndPoint.toString());
		params.put(PARAM_END_POINT_LONGITUDE, longitudeEndPoint.toString());
		params.put(PARAM_END_POINT_ADDRESS, addressEndPoint);
		
		String script = JavascriptFactory.createScriptWithAppResponseContainingParameters(getIdentifier(), params);
		Specification spec = SpecificationFactory.createWebLinkSpecWithJavascript(uri, script, params);
		
		return spec;
	}

	@Override
	public ViewGroup getViewGroup(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_googlemaps_directions, null);
		btnStartPoint = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_maps_start);
		btnStartPoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Pick Start Point");
				final List<LocationType> startPointLocationTypes = LocationType.getStartPointLocationTypes();
				LocationTypeListAdapter adapter = new LocationTypeListAdapter(activity, startPointLocationTypes);

				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectStartPointLocationType(activity, startPointLocationTypes.get(which));
					}
				});
				builder.create().show();
			}

		});
		btnEndPoint = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_maps_end);
		btnEndPoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectEndPointLocationType(activity, LocationType.PICK_LOCATION);
			}

		});
		
		setLocationListener(activity);

		return layoutTemplate;
	}
	
	private void setLocationListener(Activity activity){
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	myLocation = location;
		    	latitudeStartPoint = location.getLatitude();
		    	longitudeStartPoint = location.getLongitude();
		    	addressStartPoint = "My Location";
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	private void selectStartPointLocationType(Activity activity, LocationType locationType) {
		startPointLocationType = locationType;
		if (startPointLocationType == LocationType.PICK_LOCATION) {
			Intent intent = PickLocationActivity.getPickLocationAtLastKnownLocationIntent(activity);
			requestedStartPoint = true;
			requestedEndPoint = false;
			activity.startActivityForResult(intent, getRequestCode());
		} else {
			btnStartPoint.setText(startPointLocationType.getName());
		}
	}

	private void selectEndPointLocationType(Activity activity, LocationType locationType) {
		endPointLocationType = locationType;
		Intent intent = PickLocationActivity.getPickLocationAtLastKnownLocationIntent(activity);
		requestedEndPoint = true;
		requestedStartPoint = false;
		activity.startActivityForResult(intent, getRequestCode());
	}

	@Override
	public String getIdentifier() {
		return "template.googlemaps.directions";
	}
	

	@Override
	public void execute(AndroidClientActionResponse appResponse, Activity context) {
		String uri = appResponse.getParameters().get(PARAM_URI);
		uri = Utils.decodeAmpersant(uri);
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		context.startActivity(intent);
	}

	@Override
	public boolean isSpecificationBasedOnTemplate(Specification spec) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object preInitComponentsForEdit(Activity activity, Specification spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPreInitLongRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initComponentsForEdit(Activity activity, Specification spec, Object object) {
		for(Property prop : spec.blueprint.superBlock.properties){
			if(PARAM_START_POINT_LATITUDE.equals(prop.getName())){
				latitudeStartPoint = Double.parseDouble(prop.getText());
			}
			else if(PARAM_START_POINT_LONGITUDE.equals(prop.getName())){
				longitudeStartPoint = Double.parseDouble(prop.getText());
			}
			else if(PARAM_START_POINT_ADDRESS.equals(prop.getName())){
				addressStartPoint = prop.getText();
			}
			if(PARAM_END_POINT_LATITUDE.equals(prop.getName())){
				latitudeEndPoint = Double.parseDouble(prop.getText());
			}
			else if(PARAM_END_POINT_LONGITUDE.equals(prop.getName())){
				longitudeEndPoint = Double.parseDouble(prop.getText());
			}
			else if(PARAM_END_POINT_ADDRESS.equals(prop.getName())){
				addressEndPoint = prop.getText();
			}
		}
		btnStartPoint.setText(addressStartPoint);
		btnEndPoint.setText(addressEndPoint);
	}

	@Override
	public boolean validateComponents(Activity activity) {
		if(latitudeStartPoint == null || longitudeStartPoint == null || addressStartPoint == null){
			NotificationUtils.showFeedbackMessage(activity, "Choose a start point.");
			return false;
		}
		if(latitudeEndPoint == null || longitudeEndPoint == null || addressEndPoint == null){
			NotificationUtils.showFeedbackMessage(activity, "Choose an end point.");
			return false;
		}
		return true;
	}

	@Override
	public String generateDescription(Activity activity) {
		return "Directions from " +  addressStartPoint + " to " + addressEndPoint;
	}

	@Override
	public boolean canHandleData(Intent data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initWithData(Activity activity, Intent data) {
		// TODO Auto-generated method stub

	}

	public class LocationTypeListAdapter extends ArrayAdapter<LocationType> {

		private LayoutInflater layoutInflater;

		public LocationTypeListAdapter(Context context, List<LocationType> objects) {
			super(context, R.id.lbl_app_template_googlemaps_location_name, objects);
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LocationType locationType = getItem(position);
			if (convertView == null) {
				convertView = (View) layoutInflater.inflate(R.layout.app_template_googlemaps_locationtype, null);
			}

			TextView txtName = (TextView) convertView.findViewById(R.id.lbl_app_template_googlemaps_location_name);
			txtName.setText(locationType.getName());

			TextView txtDescription = (TextView) convertView.findViewById(R.id.lbl_app_template_googlemaps_location_description);
			txtDescription.setText(locationType.getDescription());

			return convertView;
		}

	}

}
