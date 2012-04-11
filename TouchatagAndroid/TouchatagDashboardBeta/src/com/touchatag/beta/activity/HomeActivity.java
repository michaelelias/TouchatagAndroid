package com.touchatag.beta.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.touchatag.beta.R;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.Server;
import com.touchatag.beta.store.SettingsStore;

public class HomeActivity extends TabActivity {

	private SettingsStore settingsStore;
	private AssociationStore assStore;
	private TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);

		settingsStore = new SettingsStore(this);
		assStore = new AssociationStore(this);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		TabSpec appsTabSpec = tabHost.newTabSpec("tid1");
		TabSpec tagsTabSpec = tabHost.newTabSpec("tid1");

		Resources resources = getResources();
		appsTabSpec.setIndicator("Applications", resources.getDrawable(R.drawable.home_tab_apps));
		tagsTabSpec.setIndicator("Tags", resources.getDrawable(R.drawable.home_tab_tags));

		appsTabSpec.setContent(new Intent(this, AppsActivity.class));
		tagsTabSpec.setContent(new Intent(this, TagsActivity.class));

		tabHost.addTab(appsTabSpec);
		tabHost.addTab(tagsTabSpec);

		if (settingsStore.isAuthorized()) {
			showApplicationTab();
		} else {
			startAuthorization();
		}
	}

	private void startAuthorization() {
		Server server = settingsStore.getServer();
		if (server.getKey() != null && server.getSecret() != null) {
			Intent intent = new Intent(this, AuthorizeActivity.class);
			startActivity(intent);
		}
	}

	private void showApplicationTab() {
		tabHost.getTabWidget().setCurrentTab(0);
	}

}
