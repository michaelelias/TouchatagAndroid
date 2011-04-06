package com.touchatag.android.activity.template;

import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public abstract class BaseTemplate implements Template {

	private String name;
	private String description;
	private String packageName;
	private Drawable icon;
	
	protected BaseTemplate(String name, String description, String packageName) {
		this.name = name;
		this.description = description;
		this.packageName = packageName;
	}
	
	public PackageInfo getPackageInfo(PackageManager packageManager) {
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);
		for (PackageInfo info : packages) {
			Log.i("PackageName4" + name, info.packageName);
			if (info.packageName.equals(packageName)) {
				return info;
			}
		}
		return null;
	}

	public Drawable getIcon(PackageManager packageManager) {
		if(icon == null){
			PackageInfo info = getPackageInfo(packageManager);
			if (info != null) {
				icon = info.applicationInfo.loadIcon(packageManager);
			}
		}
		return icon;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getPackageName() {
		return packageName;
	}
}
