package com.touchatag.android.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.client.FoursquareRestClient;
import com.touchatag.android.client.rest.model.FoursquareVenue;
import com.touchatag.android.client.rest.model.specification.Property;
import com.touchatag.android.client.rest.model.specification.Specification;
import com.touchatag.android.client.rest.model.specification.SpecificationFactory;

public class FoursquareVenueTemplate extends BaseTemplate {

	public FoursquareVenueTemplate() {
		super("Foursquare Venue", "Opens the Foursquare app at a venue's detail view", "com.joelapenna.foursquared");
		// TODO Auto-generated constructor stub
	}

	private static final int TAG_VENUE_ID = 1;
	
	private String selectedVenueId;
	
	@Override
	public boolean isCorrectPackageInfo(PackageInfo packageInfo) {
		Log.i("TouchatagApp", packageInfo.packageName);
		return false;
	}
	
	@Override
	public Intent getActivityForResultIntent(Activity activity) {
		return new Intent(activity, FoursquareVenuePickerActivity.class);
	}
	
	@Override
	public ViewGroup getViewGroup(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_4sq, null);
		ImageButton btnSearch = (ImageButton) layoutTemplate.findViewById(R.id.btn_apptemplate_4sqvenue_search);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});

		return layoutTemplate;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		TextView lblVenueName = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_name);
		TextView lblVenueAddress = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_address);
		
		String venueId = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_ID);
		String venueName = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_NAME);
		String venueAddress = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_ADDRESS);
		
		selectedVenueId = venueId;
		lblVenueName.setText(venueName);
		lblVenueAddress.setText(venueAddress);
		// activity.onConfigComplete();
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		TextView lblVenueName = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_name);
		String url = "http://m.foursquare.com/venue/" + selectedVenueId;
		return SpecificationFactory.createSimpleWebLinkSpec(url);
	}

	@Override
	public String generateAppName(final Activity activity, Intent data) {
		TextView lblVenueName = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_name);
		return "4SQ " + lblVenueName.getText().toString();
	}

	@Override
	public int getRequestCode() {
		return 103;
	}

	@Override
	public boolean isSpecificationBasedOnTemplate(Specification spec) {
		if (spec != null) {
			Property prop = spec.blueprint.superBlock.breakdown.blocks.get(0).properties.get(0);
			Object value = prop.value;
			return true;
		}
		return false;
	}

	@Override
	public Object preInitComponentsForEdit(Activity activity, Specification spec) {
		Property prop = spec.blueprint.superBlock.properties.get(0);
		String uri = (String) prop.value;
		String venueId = uri.substring(uri.lastIndexOf("/") + 1);
		FoursquareRestClient client = new FoursquareRestClient();
		return client.findVenueById(venueId);	
	}

	@Override
	public boolean isPreInitLongRunning() {
		return true;
	}

	@Override
	public void initComponentsForEdit(Activity activity, Specification spec, Object object) {
		TextView lblVenueName = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_name);
		TextView lblVenueAddress = (TextView) activity.findViewById(R.id.txt_apptemplate_4sqvenue_address);
		
		if(object instanceof FoursquareVenue){
			FoursquareVenue venue = (FoursquareVenue)object;
			selectedVenueId = venue.getId();
			lblVenueName.setText(venue.getName());
			lblVenueAddress.setText(venue.getAddressAsFormattedString());
		}
	}

}
