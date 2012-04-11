package com.touchatag.beta.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.touchatag.acs.api.client.model.specification.Property;
import com.touchatag.acs.api.client.model.specification.Specification;
import com.touchatag.beta.R;
import com.touchatag.foursquare.api.client.FoursquareRestClient;
import com.touchatag.foursquare.api.client.model.Venue;

public class FoursquareVenueTemplate extends BaseTemplate {

	public FoursquareVenueTemplate() {
		super("Foursquare Venue", "Opens the Foursquare app at a venue's detail view", "com.joelapenna.foursquared");
		// TODO Auto-generated constructor stub
	}

	private static final int TAG_VENUE_ID = 1;
	
	private String selectedVenueId;
	private String selectedVenueName;
	private String selectedVenueAddress;
	
	private CheckBox cbxCheckinServerSide;
	
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
		Button btnSearch = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_4sqvenue);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});
		cbxCheckinServerSide = (CheckBox)layoutTemplate.findViewById(R.id.cbx_apptemplate_4sq_serverside);
		
		return layoutTemplate;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		Button btnSearch = (Button) activity.findViewById(R.id.btn_apptemplate_4sqvenue);
		
		selectedVenueId = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_ID);
		selectedVenueName = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_NAME);
		selectedVenueAddress = data.getStringExtra(FoursquareVenuePickerActivity.EXTRA_VENUE_ADDRESS);
		
		String venueDescriptionHtml = "<b>" + selectedVenueName + "</b>" + "<br/>" + selectedVenueAddress;
		btnSearch.setText(Html.fromHtml(venueDescriptionHtml));
		// activity.onConfigComplete();
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		boolean doServerSide = cbxCheckinServerSide.isChecked();
		if(doServerSide){
			return null;
		} else {
			String url = "http://m.foursquare.com/venue/" + selectedVenueId;
			return SpecificationFactory.createSimpleWebLinkSpec(url);
		}
	}

	@Override
	public String generateDescription(final Activity activity) {
		return "4SQ Venue @ " + selectedVenueName;
	}

	@Override
	public int getRequestCode() {
		return 103;
	}

	@Override
	public boolean isSpecificationBasedOnTemplate(Specification spec) {
		if (spec != null) {
			Property prop = spec.blueprint.superBlock.breakdown.blocks.get(0).properties.get(0);
			Object value = prop.getText();
			return true;
		}
		return false;
	}

	@Override
	public Object preInitComponentsForEdit(Activity activity, Specification spec) {
		Property prop = spec.blueprint.superBlock.properties.get(0);
		String uri = prop.getUri();
		String venueId = uri.substring(uri.lastIndexOf("/") + 1);
		FoursquareRestClient client = new FoursquareRestClient(){

			@Override
			public void log(String message) {
				Log.i("FoursquareRestClient", message);
			}
			
		};
		return client.findVenueById(venueId);	
	}

	@Override
	public boolean isPreInitLongRunning() {
		return true;
	}

	@Override
	public void initComponentsForEdit(Activity activity, Specification spec, Object object) {
		Button btnSearch = (Button) activity.findViewById(R.id.btn_apptemplate_4sqvenue);
		Drawable imgVenue = activity.getResources().getDrawable(R.drawable.place);
		btnSearch.setCompoundDrawablesWithIntrinsicBounds(imgVenue, null, null, null);
		
		if(object instanceof Venue){
			Venue venue = (Venue)object;
			selectedVenueId = venue.getId();
			selectedVenueName = venue.getName();
			selectedVenueAddress = venue.getAddressAsFormattedString();
			String venueDescriptionHtml = "<b>" + selectedVenueName + "</b>" + "<br/>" + selectedVenueAddress;
			btnSearch.setText(Html.fromHtml(venueDescriptionHtml));
		}
	}
	
	@Override
	public boolean validateComponents(Activity activity){
//		EditText txtMailTo = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
//		if(txtMailTo.getText().length() == 0){
//			NotificationUtils.showFeedbackMessage(activity, "Email address cannot be empty");
//			return false;
//		}
		return true;
	}
	
	@Override
	public boolean canHandleData(Intent data) {
//		Uri uri = data.getData();
//		Cursor c = activity.getContentResolver().query(uri, new String[]{Contacts.}, null, null, null);
//		try {
//		    c.moveToFirst();
//		    String displayName = c.getString(0);
//		} finally {
//		    c.close();
//		}
		return false;
	}
	
	@Override
	public void initWithData(Activity activity, Intent data) {
//		String uri = data.getStringExtra(Intent.EXTRA_TEXT);
//		String title = data.getStringExtra(Intent.EXTRA_TITLE);
//		
//		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
//		txtUri.setText(uri);
//		
//		EditText txtName = (EditText) activity.findViewById(R.id.txt_appdetail_name);
//		txtName.setText(title);
	}

}
