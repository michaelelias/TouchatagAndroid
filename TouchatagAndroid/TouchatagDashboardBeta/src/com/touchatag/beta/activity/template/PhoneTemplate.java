package com.touchatag.beta.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.touchatag.acs.api.client.model.specification.Property;
import com.touchatag.acs.api.client.model.specification.Specification;
import com.touchatag.beta.R;
import com.touchatag.beta.util.NotificationUtils;

public class PhoneTemplate extends BaseTemplate {

	private String phoneNumber;
	
	public PhoneTemplate() {
		super("Phone Dialer", "Launches the phone dialer for a certain number", "com.android.phone");
	}

	@Override
	public boolean isCorrectPackageInfo(PackageInfo packageInfo) {
		Log.i("TouchatagApp", packageInfo.packageName);
		return false;
	}

	@Override
	public Intent getActivityForResultIntent(Activity activity) {
		return new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
	}
	
	@Override
	public ViewGroup getViewGroup(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_phone, null);
		Button btnBrowse = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_phone);
		btnBrowse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});
		
		EditText txtPhoneNumber = (EditText)layoutTemplate.findViewById(R.id.txt_apptemplate_phone);
		txtPhoneNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				phoneNumber = s.toString();
			}
		});

		return layoutTemplate;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_phone);
		String id = data.getData().getLastPathSegment();

		Cursor cursor = activity.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);
		cursor.moveToFirst();
		try {
			phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.DATA));
			txtPhoneNumber.setText(phoneNumber);
		} catch (Exception e) {
			NotificationUtils.showFeedbackMessage(activity, "Contact doesn't have a phone number");
		}
		// activity.onConfigComplete();
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_phone);
		String tel = txtPhoneNumber.getText().toString();
		String url = "tel:" + tel;
		return SpecificationFactory.createSimpleWebLinkSpec(url);
	}

	@Override
	public int getRequestCode() {
		return 101;
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
	public String generateDescription(Activity activity) {
		return phoneNumber;
	}

	@Override
	public Object preInitComponentsForEdit(Activity activity, Specification spec) {
		return null;
	}

	@Override
	public boolean isPreInitLongRunning() {
		return false;
	}

	@Override
	public void initComponentsForEdit(Activity activity, Specification spec, Object object) {
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_phone);
		Property prop = spec.blueprint.superBlock.properties.get(0);
		String uri = prop.getUri();
		phoneNumber = uri.substring(4);
		txtPhoneNumber.setText(phoneNumber);
	}
	
	@Override
	public boolean validateComponents(Activity activity){
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_phone);
		if(txtPhoneNumber.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(activity, "Phone number cannot be empty");
			return false;
		}
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
