package com.touchatag.beta.activity.template;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import com.touchatag.beta.util.Utils;

public class WeblinkTemplate extends BaseTemplate {

	private String uri;
	
	public WeblinkTemplate() {
		super("Web link", "Launches the browser with an URL", "com.android.browser");
	}

	@Override
	public boolean isCorrectPackageInfo(PackageInfo packageInfo) {
		Log.i("TouchatagApp", packageInfo.packageName);
		return false;
	}
	
	public Intent getActivityForResultIntent(Activity activity) {
		return new Intent(activity, BookmarkPickerActivity.class);
	}
	
	@Override
	public ViewGroup getViewGroup(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		RelativeLayout layoutTemplate = (RelativeLayout) inflater.inflate(R.layout.app_template_weblink, null);
		Button btnBrowse = (Button) layoutTemplate.findViewById(R.id.btn_apptemplate_weblink);
		btnBrowse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivityForResult(getActivityForResultIntent(activity), getRequestCode());
			}

		});
		
		EditText txtUri = (EditText)layoutTemplate.findViewById(R.id.txt_apptemplate_weblink);
		txtUri.addTextChangedListener(new TextWatcher() {
			
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
				uri = s.toString();
			}
		});

		return layoutTemplate;
	}

	
	
	@Override
	public String generateDescription(Activity activity) {
		return uri;
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		uri = data.getStringExtra(BookmarkPickerActivity.EXTRA_BOOKMARK_URL);
		txtUri.setText(uri);
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		String uri = txtUri.getText().toString();
		uri = Utils.encodeAmpersant(uri);
		return SpecificationFactory.createSimpleWebLinkSpec(uri);
	}

	@Override
	public int getRequestCode() {
		return 102;
	}

	@Override
	public boolean isSpecificationBasedOnTemplate(Specification spec) {
		if (spec != null) {
			Property prop = spec.blueprint.superBlock.breakdown.blocks.get(0).properties.get(0);
			Object value = prop.getUri();
			return true;
		}
		return false;
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
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		Property prop = spec.blueprint.superBlock.properties.get(0);
		uri = prop.getUri();
		txtUri.setText(uri);
	}
	
	@Override
	public boolean validateComponents(Activity activity){
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		if(txtUri.getText().length() == 0){
			NotificationUtils.showFeedbackMessage(activity, "Provide an URI");
			return false;
		}
		try {
			new URI(txtUri.getText().toString());
		} catch (URISyntaxException e) {
			NotificationUtils.showFeedbackMessage(activity, "Not a valid URI");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHandleData(Intent data) {
		String text = data.getStringExtra(Intent.EXTRA_TEXT);
		try {
			new URI(text);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	@Override
	public void initWithData(Activity activity, Intent data) {
		String uri = data.getStringExtra(Intent.EXTRA_TEXT);
		String title = data.getStringExtra(Intent.EXTRA_TITLE);
		String subject = data.getStringExtra(Intent.EXTRA_SUBJECT);
		
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		txtUri.setText(uri);
		
		EditText txtName = (EditText) activity.findViewById(R.id.txt_appdetail_name);
		txtName.setText(title != null ? title : subject);
	}
	
}
