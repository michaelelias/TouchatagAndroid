package com.touchatag.beta.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
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

public class MailTemplate extends BaseTemplate {

	private String contactId;
	private String mailAddress;
	
	public MailTemplate() {
		super("Email", "Opens the mail client with an email address", "com.google.android.email");
		// TODO Auto-generated constructor stub
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
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_mail, null);
		Button btnBrowse = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_mail);
		btnBrowse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});
		
		EditText txtMail = (EditText)layoutTemplate.findViewById(R.id.txt_apptemplate_mail);
		txtMail.addTextChangedListener(new TextWatcher() {
			
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
				mailAddress = s.toString();
			}
		});

		return layoutTemplate;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		EditText txtEmail = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
		contactId = data.getData().getLastPathSegment();
		
		Cursor cursor = activity.getContentResolver().query(Email.CONTENT_URI, null, Email.CONTACT_ID + "=?", new String[] { contactId }, null);
		cursor.moveToFirst();
		try {
			String email = cursor.getString(cursor.getColumnIndex(Email.DATA));
			mailAddress = email;
			txtEmail.setText(email);
		} catch (Exception e) {
			NotificationUtils.showFeedbackMessage(activity, "Contact doesn't have an email address");
		}
		// activity.onConfigComplete();
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
		String tel = txtPhoneNumber.getText().toString();
		String url = "mailto:" + tel;
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
		return mailAddress;
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
		EditText txtMailTo = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
		Property prop = spec.blueprint.superBlock.properties.get(0);
		String uri = prop.getUri();
		mailAddress = uri.substring(4);
		txtMailTo.setText(mailAddress);
	}

	@Override
	public boolean validateComponents(Activity activity){
		EditText txtMailTo = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
		if(txtMailTo.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(activity, "Provide and email address.");
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
