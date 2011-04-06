package com.touchatag.android.activity.template;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.touchatag.android.client.rest.model.specification.Specification;

public interface Template {

	public boolean isCorrectPackageInfo(PackageInfo packageInfo);

	public int getRequestCode();

	public Intent getActivityForResultIntent(Activity activity);

	public void processActivityResult(final Activity activity, Intent data);

	public Specification createSpecification(final Activity activity);

	public ViewGroup getViewGroup(Activity activity);
	
	public PackageInfo getPackageInfo(PackageManager packageManager);

	public Drawable getIcon(PackageManager packageManager);

	public boolean isSpecificationBasedOnTemplate(Specification spec);

	/**
	 * Hook method called before <code>initComponentsForEdit</code> is called. Can be used to load data...
	 * 
	 * @param activity The activity that is hosting the template's components
	 * @param spec The specification of the application
	 * @return An object which is passed to <code>initComponentsForEdit</code>. Up to implementors to decide what it should be.
	 */
	public Object preInitComponentsForEdit(Activity activity, Specification spec);
	
	/**
	 * Hint if the pre init is a long running task. If it is, a progress dialog will be shown.
	 * 
	 * @return
	 */
	public boolean isPreInitLongRunning();
	
	/**
	 * Initializes template's components for editing. 
	 * 
	 * @param activity The activity that is hosting the template's components
	 * @param spec The specification of the application
	 * @param object The object obtained from calling <code>preInitComponentsForEdit</code>
	 */
	public void initComponentsForEdit(Activity activity, Specification spec, Object object);

	/**
	 * Generates an appropriate name for the application.
	 * This method is called after the <code>processActivityResult</code> has been called and if no name has been provided for the application. 
	 * 
	 * @return The generated name
	 */
	public String generateAppName(Activity activity, Intent data);
	
	public String getName();
	
	public String getDescription();

	public String getPackageName();
	
}
