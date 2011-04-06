package com.touchatag.android.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.touchatag.android.R;
import com.touchatag.android.client.rest.model.specification.Property;
import com.touchatag.android.client.rest.model.specification.Specification;
import com.touchatag.android.client.rest.model.specification.SpecificationFactory;

public class WeblinkTemplate extends BaseTemplate {

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

		return layoutTemplate;
	}

	
	
	@Override
	public String generateAppName(Activity activity, Intent data) {
		return "";
	}

	@Override
	public void processActivityResult(final Activity activity, Intent data) {
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		String url = data.getStringExtra(BookmarkPickerActivity.EXTRA_BOOKMARK_URL);
		txtUri.setText(url);
	}

	@Override
	public Specification createSpecification(final Activity activity) {
		EditText txtUri = (EditText) activity.findViewById(R.id.txt_apptemplate_weblink);
		String uri = txtUri.getText().toString();
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
			Object value = prop.value;
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
		String uri = (String) prop.value;
		txtUri.setText(uri.substring(4));
	}

}
