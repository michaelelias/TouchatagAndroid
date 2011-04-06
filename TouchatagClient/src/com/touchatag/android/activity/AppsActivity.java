package com.touchatag.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.TouchatagApplication;
import com.touchatag.android.activity.correlation.CorrelationDefinitionConsistency;
import com.touchatag.android.activity.template.TouchatagTemplate;
import com.touchatag.android.client.TouchatagRestClient;
import com.touchatag.android.client.rest.model.Application;
import com.touchatag.android.client.rest.model.CorrelationDefinition;
import com.touchatag.android.client.rest.model.Page;
import com.touchatag.android.store.ApplicationStore;
import com.touchatag.android.store.AssociationStore;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.util.NotificationUtils;

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
	private Page<Application> pageApps = new Page<Application>();
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
		pageApps = appStore.getPage(1, 25);

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

	}

	@Override
	protected void onStart() {
		super.onStart();
		loadApplications();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.apps_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_apps_newapp:
			startActivity(new Intent(this, AppActivity.class));
			return true;
		case R.id.menuitem_apps_refresh:
			forceRefresh = true;
			loadApplications();
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
			Application app = pageApps.getItems().get(info.position);
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
							DeleteApplicationAsyncTask task = new DeleteApplicationAsyncTask();
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

	private boolean loadApplications() {
		if (settingsStore.isAuthorized()) {
			pageApps = appStore.getPage(1, 25);
			if ((pageApps.getItems().size() == 0 && !TouchatagApplication.appsLoaded) || forceRefresh) {
				TouchatagRestClient restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
				LoadAppPageAsyncTask loadAppsTask = new LoadAppPageAsyncTask();
				loadAppsTask.execute(restClient);
				forceRefresh = false;
			} else {
				listAdapter.notifyDataSetChanged();
			}
			return true;
		}
		return false;
	}

	private void deleteApplication(int position) {
		appToRemove = pageApps.getItems().get(position);
		showDialog(DIALOG_DELETE_APP);
	}

	private void editApplication(int position) {
		Application app = pageApps.getItems().get(position);
		if (app.getSpecification() == null) {
			showDialog(DIALOG_NOT_MANAGED);
		} else {
			startActivity(AppActivity.getAppDetailIntent(AppsActivity.this, app));
		}
	}

	private boolean mergeToStore(Page<Application> appPage) {
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
			return pageApps.getItems().size();
		}

		@Override
		public Object getItem(int position) {
			return pageApps.getItems().get(position);
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

			Application app = pageApps.getItems().get(position);

			if(app.getTemplate() != null){
				TouchatagTemplate template = TouchatagTemplate.valueOf(app.getTemplate());
				Drawable templateIcon = template.getIcon(AppsActivity.this.getPackageManager());
				if(templateIcon != null){
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

	private class LoadAppPageAsyncTask extends AsyncTask<TouchatagRestClient, Void, Page<Application>> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(AppsActivity.this, null, "Loading Applications...");
		}

		@Override
		protected Page<Application> doInBackground(TouchatagRestClient... params) {
			Page<Application> apps = params[0].getApplications(1, 25);
			if (mergeToStore(apps)) {
				pageApps = appStore.getPage(1, 25);
			}
			CorrelationDefinition corrDef = params[0].getCorrelationDefinition();
			assStore.update(corrDef);
			boolean modified = CorrelationDefinitionConsistency.cleanup(corrDef, appStore);
			if (modified) {
				corrDef = params[0].updateCorrelationDefinition(corrDef);
				assStore.update(corrDef);
			}
			return pageApps;
		}

		@Override
		protected void onPostExecute(Page<Application> result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			listAdapter.notifyDataSetChanged();
			TouchatagApplication.appsLoaded = true;
		}

	}

	private class LoadCorrelationDefinitionAsyncTask extends AsyncTask<TouchatagRestClient, Void, CorrelationDefinition> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected CorrelationDefinition doInBackground(TouchatagRestClient... params) {
			return params[0].getCorrelationDefinition();
		}

		@Override
		protected void onPostExecute(CorrelationDefinition corrDef) {
			super.onPostExecute(corrDef);
			assStore.update(corrDef);
		}

	}

	private class DeleteApplicationAsyncTask extends AsyncTask<Application, Void, Boolean> {

		private ProgressDialog progressDialog;
		private Application app;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(AppsActivity.this, null, "Deleting application");
		}

		@Override
		protected Boolean doInBackground(Application... params) {
			app = params[0];
			TouchatagRestClient restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
			boolean deleted = restClient.deleteApplication(app);
			if (deleted) {
				appStore.remove(app);
				pageApps.getItems().remove(app);
			}
			return deleted;
		}

		@Override
		protected void onPostExecute(Boolean deleted) {
			super.onPostExecute(deleted);
			progressDialog.dismiss();
			String message = "";
			if (deleted) {
				message = "Deleted application ''";
			} else {
				message = "Couldn't delete application";
			}
			NotificationUtils.showFeedbackMessage(AppsActivity.this, message);
			listAdapter.notifyDataSetChanged();

		}

	}
}
