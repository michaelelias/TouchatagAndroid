package com.touchatag.beta.activity.template;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.touchatag.beta.R;
import com.touchatag.beta.util.NotificationUtils;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.MarkerRenderer;
import de.android1.overlaymanager.OverlayManager;

public class PickLocationActivity extends MapActivity {

	private static final int DIALOG_PICK_LOCATION = 1;

	public static final String EXTRA_LATITUDE = "location.latitude";
	public static final String EXTRA_LONGITUDE = "location.longitude";
	public static final String EXTRA_ADDRESS = "location.address";
	private static final String EXTRA_LAST_KNOW_LOCATION = "location.lastknown";
	private MapView mapView;
	private OverlayManager overlayManager;
	private Intent pickedLocationResultIntent;

	private MyLocationOverlay myLocationOverlay;

	/**
	 * This intent will initialize the mapview to the given location
	 * 
	 */
	public static Intent getPickLocationIntent(Location location, Activity context) {
		Intent intent = new Intent(context, PickLocationActivity.class);
		intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
		intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
		return intent;
	}

	/**
	 * This intent will initialize the mapview to the last known location of the
	 * phone
	 * 
	 */
	public static Intent getPickLocationAtLastKnownLocationIntent(Activity context) {
		Intent intent = new Intent(context, PickLocationActivity.class);
		intent.putExtra(EXTRA_LAST_KNOW_LOCATION, true);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Pick a location");
		setContentView(R.layout.pick_location);

		mapView = (MapView) findViewById(R.id.map_picklocation);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(false);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		overlayManager = new OverlayManager(this, mapView);

		ManagedOverlay overlay = overlayManager.createOverlay();

		overlay.setOnGestureListener(new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent event) {
				GeoPoint geoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
				onPickedLocation(geoPoint);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		overlayManager.populate();
		// MapOverlay mapOverlay = new MapOverlay();
		// List<Overlay> listOfOverlays = mapView.getOverlays();
		// listOfOverlays.clear();
		// listOfOverlays.add(mapOverlay);
	}

	@Override
	protected void onResume() {
		super.onResume();

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		Intent intent = getIntent();
		if (intent.getBooleanExtra(EXTRA_LAST_KNOW_LOCATION, false)) {
			Location lastKnownLocation = getLastKnownLocation();
			MapController mc = mapView.getController();

			GeoPoint geoPoint = new GeoPoint((int) (lastKnownLocation.getLatitude() * 1E6), (int) (lastKnownLocation.getLongitude() * 1E6));
			mc.animateTo(geoPoint);
			mc.setZoom(15);
			mapView.invalidate();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}

	@Override
	protected boolean isLocationDisplayed() {
		return true;
	}

	private Location getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	private void onAddressResolvingSuccess(String address){
		pickedLocationResultIntent.putExtra(EXTRA_ADDRESS, address);
		
		String message = pickedLocationResultIntent.getStringExtra(EXTRA_ADDRESS);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick Location?") //
				.setIcon(android.R.drawable.ic_dialog_info) //
				.setMessage(message) //
				.setCancelable(false) //
				.setPositiveButton("Pick", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						setResult(RESULT_OK, pickedLocationResultIntent);
						finish();
					}
				}).setNegativeButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	private void onAddressResolvingFailed(){
		NotificationUtils.showFeedbackMessage(this, "Failed to resolve address");
	}
	
	private void onPickedLocation(GeoPoint geoPoint) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_LATITUDE, geoPoint.getLatitudeE6());
		intent.putExtra(EXTRA_LONGITUDE, geoPoint.getLongitudeE6());
		pickedLocationResultIntent = intent;

		new LoadAddressAsyncTask().execute(geoPoint);

		// ManagedOverlay overlay =
		// overlayManager.getOverlay("LocationAddressMarker");
		// if (overlay == null) {
		// overlay = overlayManager.createOverlay("LocationAddressMarker");
		// overlay.createItem(geoPoint, address);
		// overlay.setCustomMarkerRenderer(new MarkerRenderer() {
		//
		// @Override
		// public Drawable render(ManagedOverlayItem item, Drawable
		// defaultMarker, int bitState) {
		//
		// if (item.getCustomRenderedDrawable() != null)
		// return item.getCustomRenderedDrawable();
		//
		// NinePatchDrawable drawNinePatch = (NinePatchDrawable)
		// getResources().getDrawable(R.drawable.address);
		//
		// //BitmapDrawable b = (BitmapDrawable) defaultMarker;
		// //Bitmap bitmap =
		// Bitmap.createBitmap(b.getBitmap().copy(Bitmap.Config.ARGB_8888,
		// true));
		// Canvas canvas = new Canvas();
		// drawNinePatch.draw(canvas);
		// Paint p = new Paint();
		// p.setColor(Color.BLACK);
		// p.setAntiAlias(true);
		// p.setTextSize(10);
		// canvas.drawText(item.getTitle(), 30, 150, p);
		// BitmapDrawable bd = new BitmapDrawable(bitmap);
		// bd.setBounds(0, 0, 200, 40);
		//
		// // Feel free to cache the custom marker and reuse him for
		// // better
		// // performance. your choice!
		// item.setCustomRenderedDrawable(bd);
		// return bd;
		//
		// }
		// });
		// }
		// overlayManager.populate();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private class LoadAddressAsyncTask extends AsyncTask<GeoPoint, Void, String> {

		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(PickLocationActivity.this, null, "Loading Address...");
		}

		@Override
		protected String doInBackground(GeoPoint... params) {
			try {
				GeoPoint geoPoint = params[0];
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
				List<Address> addresses = geoCoder.getFromLocation(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, 1);
				String address = "";
				if (addresses.size() > 0) {
					for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
						address += addresses.get(0).getAddressLine(i) + " ";
				}
				return address;
			} catch(IOException e){
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String address) {
			super.onPostExecute(address);
			progressDialog.dismiss();
			if(address != null){
				onAddressResolvingSuccess(address);
			} else {
				onAddressResolvingFailed();
			}
		}

		
	}
}
