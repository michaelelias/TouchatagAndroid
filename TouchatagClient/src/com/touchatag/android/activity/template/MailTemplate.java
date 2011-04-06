package com.touchatag.android.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.touchatag.android.R;
import com.touchatag.android.client.rest.model.specification.Property;
import com.touchatag.android.client.rest.model.specification.Specification;
import com.touchatag.android.client.rest.model.specification.SpecificationFactory;
import com.touchatag.android.util.NotificationUtils;

public class MailTemplate extends BaseTemplate {

	private String contactId;
	
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
			Object value = prop.value;
			return true;
		}
		return false;
	}
	
	@Override
	public String generateAppName(Activity activity, Intent data) {
//		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_phone);
//		String id = data.getData().getLastPathSegment();
//
//		Cursor cursor = activity.getContentResolver().query(Contacts.CONTENT_URI, null, Email.CONTACT_ID + "=?", new String[] { id }, null);
//		cursor.moveToFirst();
		return null;
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
		EditText txtPhoneNumber = (EditText) activity.findViewById(R.id.txt_apptemplate_mail);
		Property prop = spec.blueprint.superBlock.properties.get(0);
		String uri = (String) prop.value;
		txtPhoneNumber.setText(uri.substring(4));
	}

}
