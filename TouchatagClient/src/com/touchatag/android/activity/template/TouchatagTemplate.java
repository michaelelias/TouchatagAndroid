package com.touchatag.android.activity.template;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.touchatag.android.client.rest.model.Application;
import com.touchatag.android.client.rest.model.specification.Specification;


public enum TouchatagTemplate implements Template{

	FOURSQUARE_VENUE{

		private FoursquareVenueTemplate instance;
		
		@Override
		public BaseTemplate getTemplateInstance() {
			if(instance == null){
				instance = new FoursquareVenueTemplate();
			}
			return instance; 
		}
		
	}, //
	PHONE {

		private PhoneTemplate instance;
		
		@Override
		public BaseTemplate getTemplateInstance() {
			if(instance == null){
				instance = new PhoneTemplate();
			}
			return instance; 
		}
		
	}, //
	MAIL {

		private MailTemplate instance;
		
		@Override
		public BaseTemplate getTemplateInstance() {
			if(instance == null){
				instance = new MailTemplate();
			}
			return instance; 
		}
		
	}, //
	SMS {

		private SMSTemplate instance;
		
		@Override
		public BaseTemplate getTemplateInstance() {
			if(instance == null){
				instance = new SMSTemplate();
			}
			return instance; 
		}
		
	}, //
	BROWSER {

		private WeblinkTemplate instance;
		
		@Override
		public BaseTemplate getTemplateInstance() {
			if(instance == null){
				instance = new WeblinkTemplate();
			}
			return instance; 
		}
		
	};

	public abstract BaseTemplate getTemplateInstance();

	public static List<TouchatagTemplate> getTemplateList() {
		return Arrays.asList(TouchatagTemplate.values());
	}
	
	public static BaseTemplate findTemplateFromSpecification(Specification spec) {
		for (TouchatagTemplate template : TouchatagTemplate.values()) {
			BaseTemplate templateInstance = template.getTemplateInstance();
			if (templateInstance.isSpecificationBasedOnTemplate(spec)) {
				return templateInstance;
			}
		}
		return null;
	}
	
	public static TouchatagTemplate getTemplateFromApplication(Application app){
		if(app.getTemplate() != null){
			return TouchatagTemplate.valueOf(app.getTemplate());
		}
		return null;
	}
	
	public boolean isCorrectPackageInfo(PackageInfo packageInfo){
		return getTemplateInstance().isCorrectPackageInfo(packageInfo);
	}

	public int getRequestCode(){
		return getTemplateInstance().getRequestCode();
	}

	public Intent getActivityForResultIntent(Activity activity){
		return getTemplateInstance().getActivityForResultIntent(activity);
	}

	public void processActivityResult(final Activity activity, Intent data){
		getTemplateInstance().processActivityResult(activity, data);
	}

	public Specification createSpecification(final Activity activity){
		return getTemplateInstance().createSpecification(activity);
	}

	public ViewGroup getViewGroup(Activity activity){
		return getTemplateInstance().getViewGroup(activity);
	}
	
	public PackageInfo getPackageInfo(PackageManager packageManager){
		return getTemplateInstance().getPackageInfo(packageManager);
	}

	public Drawable getIcon(PackageManager packageManager){
		return getTemplateInstance().getIcon(packageManager);
	}

	public boolean isSpecificationBasedOnTemplate(Specification spec){
		return getTemplateInstance().isSpecificationBasedOnTemplate(spec);
	}

	public String generateAppName(Activity activity, Intent data){
		return getTemplateInstance().generateAppName(activity, data);
	}
	
	@Override
	public Object preInitComponentsForEdit(Activity activity, Specification spec) {
		return getTemplateInstance().preInitComponentsForEdit(activity, spec);
	}

	@Override
	public boolean isPreInitLongRunning() {
		return getTemplateInstance().isPreInitLongRunning();
	}

	public void initComponentsForEdit(Activity activity, Specification spec, Object object){
		getTemplateInstance().initComponentsForEdit(activity, spec, object);
	}

	public String getName(){
		return getTemplateInstance().getName();
	}
	
	public String getDescription(){
		return getTemplateInstance().getDescription();
	}

	public String getPackageName(){
		return getTemplateInstance().getPackageName();
	}
	
}
