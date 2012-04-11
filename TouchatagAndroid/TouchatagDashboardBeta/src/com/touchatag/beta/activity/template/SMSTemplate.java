package com.touchatag.beta.activity.template;

import java.util.Map;
import java.util.TreeMap;

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
import com.touchatag.beta.client.soap.model.response.AndroidClientActionResponse;
import com.touchatag.beta.util.NotificationUtils;

public class SMSTemplate extends BaseTemplate {

	private static final String PARAM_URI = "smsuri";
	private static final String PARAM_NUMBER = "smsnumber";
	private static final String PARAM_BODY = "smsbody";
	
	private String smsNumber;
	private String smsBody;
	
	public SMSTemplate() {
		super("SMS", "Opens the SMS app", "com.android.mms");
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
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_sms, null);
		Button btnBrowse = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_pickcontact);
		btnBrowse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});

		EditText txtSmsNumber = (EditText)layoutTemplate.findViewById(R.id.txt_apptemplate_smsnumber);
		txtSmsNumber.addTextChangedListener(new TextWatcher() {
			
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
				smsNumber = s.toString();
			}
		});
		EditText txtSmsBody = (EditText)layoutTemplate.findViewById(R.id.txt_apptemplate_smsbody);
		txtSmsBody.addTextChangedListener(new TextWatcher() {
			
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
				smsBody = s.toString();
			}
		});
		return layoutTemplate;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_smsnumber);
		String id = data.getData().getLastPathSegment();

		Cursor cursor = activity.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);
		cursor.moveToFirst();
		try {
			smsNumber = cursor.getString(cursor.getColumnIndex(Phone.DATA));
			txtPhoneNumber.setText(smsNumber);
		} catch (Exception e) {
			NotificationUtils.showFeedbackMessage(activity, "Contact doesn't have a phone number");
		}
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		EditText txtSmsNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_smsnumber);
		EditText txtSmsBody = (EditText) activity.findViewById(R.id.txt_apptemplate_smsbody);
		String smsNumber = txtSmsNumber.getText().toString();
		String smsBody = txtSmsBody.getText().toString();
		String url = "sms:" + smsNumber;
		
		Map<String, String> params = new TreeMap<String, String>();
		params.put(PARAM_URI, url);
		params.put(PARAM_NUMBER, smsNumber);
		params.put(PARAM_BODY, smsBody);
		String script = JavascriptFactory.createScriptWithAppResponseContainingParameters(getIdentifier(), params);
		Specification spec = SpecificationFactory.createWebLinkSpecWithJavascript(url, script, params);
		
		return spec;
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
		return "SMS " + (smsBody != null ? "'" + smsBody + "' " : "") + "to " + smsNumber;
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
		EditText txtSmsNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_smsnumber);
		EditText txtSmsBody = (EditText) activity.findViewById(R.id.txt_apptemplate_smsbody);
		for(Property prop : spec.blueprint.superBlock.properties){
			if(PARAM_NUMBER.equals(prop.getName())){
				smsNumber = prop.getText();
			}
			else if(PARAM_BODY.equals(prop.getName())){
				smsBody = prop.getText();
			}
		}
		txtSmsNumber.setText(smsNumber);
		txtSmsBody.setText(smsBody);
	}
	
	@Override
	public boolean validateComponents(Activity activity){
		EditText txtSmsNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_smsnumber);
		if(txtSmsNumber.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(activity, "Provide and SMS number");
			return false;
		}
		return true;
	}

	@Override
	public String getIdentifier() {
		return "template.sms";
	}

	@Override
	public void execute(AndroidClientActionResponse appResponse, Activity context) {
		String smsUri = appResponse.getParameters().get(PARAM_URI);
		String smsNumber = appResponse.getParameters().get(PARAM_NUMBER);
		String smsBody = appResponse.getParameters().get(PARAM_BODY);
		
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", smsBody); 
		sendIntent.putExtra("address", smsNumber);
		sendIntent.setType("vnd.android-dir/mms-sms");
		context.startActivity(sendIntent);
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
