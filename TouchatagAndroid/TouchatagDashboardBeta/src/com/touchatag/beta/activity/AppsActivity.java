package com.touchatag.beta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.ApplicationApiClient;
import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.client.model.ApplicationPage;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.R;
import com.touchatag.beta.TouchatagApplication;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.activity.correlation.CorrelationDefinitionConsistency;
import com.touchatag.beta.activity.template.TouchatagTemplate;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.ApplicationStore;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.util.NotificationUtils;

public class AppsActivity extends Activity {

	private static final int DIALOG_ABOUT = 101;
	private static final int DIALOG_DELETE_APP = 102;
	private static final int DIALOG_NOT_MANAGED = 103;
	private static final int MENUITEM_DETAILS = 0;
	private static final int MENUITEM_DELETE = 1;

	public static boolean forceRefresh = false;

	private SettingsStore settingsStore;
	private ListView listView;
	private AppListAdapter listAdapter;
	private ViewGroup layoutSplash;
	private List<Application> listApps = new ArrayList<Application>();
	private CorrelationDefinition corrDef;
	private ApplicationStore appStore;
	private AssociationStore assStore;
	private Application appToRemove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.apps);

		settingsStore = new SettingsStore(this);
		appStore = new ApplicationStore(this);
		assStore = new AssociationStore(this);

		listView = (ListView) findViewById(R.id.layout_apps_list);
		listAdapter = new AppListAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				editApplication(position);
			}

		});
		registerForContextMenu(listView);
		
		layoutSplash = (ViewGroup)findViewById(R.id.layout_apps_splash);
		Button btnCreateApp = (Button)findViewById(R.id.btn_apps_create);
		Button btnRefresh = (Button)findViewById(R.id.btn_apps_refresh);
		
		btnCreateApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createApplication();
			}
		});
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshApplications();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(settingsStore.hasIdentityId()){
			fetchApplications();
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.apps_menu, menu);
		return true;
	}

	private void createApplication(){
		startActivity(new Intent(this, AppActivity.class));
	}
	
	private void refreshApplications(){
		forceRefresh = true;
		loadApplications();
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_apps_newapp:
			createApplication();
			return true;
		case R.id.menuitem_apps_refresh:
			refreshApplications();
			return true;
		case R.id.menuitem_about:
			showDialog(DIALOG_ABOUT);
			return true;
		case R.id.menuitem_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.layout_apps_list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Application app = listApps.get(info.position);
			if (app.getName() == null) {
				menu.setHeaderTitle("Unknown application");
			} else {
				menu.setHeaderTitle(app.getName());
			}
			MenuItem menuItem = menu.add(Menu.NONE, MENUITEM_DETAILS, MENUITEM_DETAILS, "Details");
			menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					editApplication(info.position);
					return true;
				}
			});
			menuItem = menu.add(Menu.NONE, MENUITEM_DELETE, MENUITEM_DELETE, "Delete");
			menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					deleteApplication(info.position);
					return true;
				}
			});
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ABOUT:
			return ActivityUtils.getAboutDialog(this);
		case DIALOG_NOT_MANAGED:
			String message = "This application was not created by this client.\n\nYou can manage it through the dashboard on the Touchatag website";

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Info") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage(message) //
					.setCancelable(false) //
					.setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			return builder.create();
		case DIALOG_DELETE_APP:
			message = "Are you sure you want to delete application " + appToRemove.getId();
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Confirm") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage(message) //
					.setCancelable(false) //
					.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							DeleteApplicationAsyncTask task = new DeleteApplicationAsyncTask("Deleting Application...", "Failed to delete application.", AppsActivity.this);
							task.execute(appToRemove);
							appToRemove = null;
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private void fetchApplications() {
		listApps = appStore.findAll(settingsStore.getIdentityId());
		setSplashScreenVisible(listApps.size() == 0);
		corrDef = assStore.getCorrelationDefinition();
		listAdapter.notifyDataSetChanged();
	}
	
	private void setSplashScreenVisible(boolean visible){
		layoutSplash.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	private void loadApplications() {
		if(settingsStore.isAuthorized()){
			LoadAppPageAsyncTask loadAppsTask = new LoadAppPageAsyncTask("Loading Applications...", "Failed to load applications.", this);
			loadAppsTask.execute();
			forceRefresh = false;
		}
	}

	private void deleteApplication(int position) {
		appToRemove = listApps.get(position);
		showDialog(DIALOG_DELETE_APP);
	}

	private void onApplicationsLoaded(ApplicationPage apps) {
		if (mergeToStore(apps)) {
			listApps = appStore.findAll(settingsStore.getIdentityId());
			listAdapter.notifyDataSetChanged();
		}
		setSplashScreenVisible(listApps.size() == 0);
		corrDef = assStore.getCorrelationDefinition();
		TouchatagApplication.appsLoaded = true;
	}

	private void onApplicationDeletedSuccess(Application app) {
		appStore.remove(app);
		listApps.remove(app);
		String message = "";
		if (app.getName() != null) {
			message = "Deleted application <b>" + app.getName() + "</b>";
		} else {
			message = "Deleted application";
		}
		NotificationUtils.showFeedbackMessage(AppsActivity.this, Html.fromHtml(message));
		listAdapter.notifyDataSetChanged();
	}

	private void onApplicationDeletedFailed(Application app) {
		NotificationUtils.showFeedbackMessage(AppsActivity.this, "Couldn't delete application");
	}

	private void editApplication(int position) {
		Application app = listApps.get(position);
		if (app.getSpecification() == null) {
			showDialog(DIALOG_NOT_MANAGED);
		} else {
			startActivity(AppActivity.getAppDetailIntent(AppsActivity.this, app));
		}
	}
	
	private boolean mergeToStore(ApplicationPage appPage) {
		boolean mergeNeeded = false;
		List<String> identifiers = new ArrayList<String>();
		Map<String, Application> applications = new HashMap<String, Application>();
		for (Application app : appPage.getItems()) {
			identifiers.add(app.getId());
			applications.put(app.getId(), app);
		}
		Map<String, Boolean> existingTags = appStore.exists(identifiers);
		for (Entry<String, Boolean> entry : existingTags.entrySet()) {
			if (!entry.getValue()) {
				mergeNeeded = true;
				appStore.store(applications.get(entry.getKey()));
			}
		}
		List<Application> staleApps = appStore.findByIdentifierNotIn(identifiers);
		for (Application staleApp : staleApps) {
			appStore.remove(staleApp);
		}
		return mergeNeeded;
	}

	private class AppListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public AppListAdapter(Context ctx) {
			layoutInflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return listApps.size();
		}

		@Override
		public Object getItem(int position) {
			return listApps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.app_item, null);
			}

			Application app = listApps.get(position);

			if (app.getTemplate() != null) {
				TouchatagTemplate template = TouchatagTemplate.valueOf(app.getTemplate());
				Drawable templateIcon = template.getIcon(AppsActivity.this.getPackageManager());
				if (templateIcon != null) {
					ImageView imgAppIcon = (ImageView) convertView.findViewById(R.id.img_appitem_icon);
					imgAppIcon.setImageDrawable(templateIcon);
				}
			}

			TextView txtAppName = (TextView) convertView.findViewById(R.id.lbl_appitem_name);
			if (app.getName() == null) {
				txtAppName.setText("Unknown application");
			} else {
				txtAppName.setText(app.getName());
			}

			TextView txtAppDescription = (TextView) convertView.findViewById(R.id.lbl_appitem_description);
			if (app.getDescription() == null) {
				txtAppDescription.setText("");
			} else {
				txtAppDescription.setText(app.getDescription());
			}

			int tagCount = corrDef.getAssociationsForCommand(app.getCommand()).size();
			ViewGroup layoutTags = (ViewGroup) convertView.findViewById(R.id.layout_appitem_tags);
			layoutTags.setVisibility(tagCount > 0 ? View.VISIBLE : View.INVISIBLE);
			if (tagCount > 0) {
				TextView lblTagCount = (TextView) convertView.findViewById(R.id.lbl_appitem_tagcount);
				lblTagCount.setText(String.valueOf(tagCount));
			}

			// convertView.setOnCreateContextMenuListener(new
			// OnCreateContextMenuListener() {
			//
			// @Override
			// public void onCreateContextMenu(ContextMenu menu, View v,
			// ContextMenuInfo menuInfo) {
			// menu.setHeaderTitle("Actions");
			// menu.addSubMenu("Edit Application");
			// menu.getItem(0).setOnMenuItemClickListener(new
			// OnMenuItemClickListener() {
			//
			// @Override
			// public boolean onMenuItemClick(MenuItem item) {
			// editApplication(position);
			// return true;
			// }
			// });
			// menu.addSubMenu("Delete Application");
			// }
			// });

			return convertView;
		}
	}

	private class LoadAppPageAsyncTask extends AcsApiAsyncTask<Void, ApplicationPage> {

		public LoadAppPageAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public ApplicationPage doApiCall(Void... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			ApplicationApiClient applicationApi = AcsApiClientFactory.createApplicationApiClient(settingsStore);
			CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			ApplicationPage apps = applicationApi.getPage(1, 25);
			CorrelationDefinition corrDef = correlationDefinitionApi.get();
			assStore.update(corrDef);
			boolean modified = CorrelationDefinitionConsistency.cleanup(corrDef, appStore);
			if (modified) {
				corrDef = correlationDefinitionApi.update(corrDef);
				assStore.update(corrDef);
			}
			return apps;
		}

		@Override
		public void processOutput(ApplicationPage output) {
			onApplicationsLoaded(output);

		}

	}

	private class DeleteApplicationAsyncTask extends AcsApiAsyncTask<Application, Boolean> {

		public DeleteApplicationAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		private Application app;

		@Override
		public Boolean doApiCall(Application... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			app = params[0];
			ApplicationApiClient applicationApi = AcsApiClientFactory.createApplicationApiClient(settingsStore);
			CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			
			boolean deleted = applicationApi.delete(app.getId());
			if (deleted) {
				CorrelationDefinition corrDef = assStore.getCorrelationDefinition();
				corrDef.removeAssociationsByApplicationId(app.getId());
				corrDef = correlationDefinitionApi.update(corrDef);
				assStore.update(corrDef);
			}
			return deleted;
		}

		@Override
		public void processOutput(Boolean deleted) {
			if (deleted) {
				onApplicationDeletedSuccess(app);
			} else {
				onApplicationDeletedFailed(app);
			}
		}
	}
	
}
