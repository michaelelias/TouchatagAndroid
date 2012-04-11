package com.touchatag.beta.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.provider.Settings.Secure;

import com.touchatag.beta.R;
import com.touchatag.beta.TouchatagApplication;
import com.touchatag.beta.activity.connections.ConnectionsActivity;
import com.touchatag.beta.store.Server;
import com.touchatag.beta.store.SettingsStore;

public class SettingsActivity extends PreferenceActivity {

	private static final int DIALOG_CLEAR_CREDENTIALS = 1;
	private static final int DIALOG_REVOKE_AUTHORIZATION = 2;
	
	private SettingsStore settingsStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_preferences);

		getPreferenceManager().setSharedPreferencesName("tt-settings");

		settingsStore = new SettingsStore(getBaseContext());

		Preference prefUsername = getPreferenceByKey(R.string.pref_username);
		prefUsername.setSummary(settingsStore.getUsername());

		Preference prefClearCredentials = getPreferenceByKey(R.string.pref_clear_credentials);
		prefClearCredentials.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DIALOG_CLEAR_CREDENTIALS);
				return true;
			}

		});
		
		Preference prefRevokeAuthorization = getPreferenceByKey(R.string.pref_revoke_authorization);
		prefRevokeAuthorization.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DIALOG_REVOKE_AUTHORIZATION);
				return true;
			}

		});
		
		Preference prefConnections = getPreferenceByKey(R.string.pref_connections);
		prefConnections.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(SettingsActivity.this, ConnectionsActivity.class));
				return true;
			}

		});

		Preference prefClientName = getPreferenceByKey(R.string.pref_clientname);
		String clientName = settingsStore.getClientName();
		if (clientName.length() > 0) {
			prefClientName.setSummary(clientName);
		}
		((EditTextPreference)prefClientName).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				settingsStore.storeClientName((String)newValue);
				return true;
			}
		});
		
		Preference prefClientId = getPreferenceByKey(R.string.pref_client_id);
		String clientId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		prefClientId.setSummary(clientId);
		
		CheckBoxPreference prefAutoLaunch = (CheckBoxPreference)getPreferenceByKey(R.string.pref_client_autolaunch);
		boolean autoLaunch = settingsStore.isAutoLaunch();
		prefAutoLaunch.setChecked(autoLaunch);
		prefAutoLaunch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				settingsStore.setAutoLaunch((Boolean)newValue);
				return true;
			}
		});
		
		ListPreference prefServer = (ListPreference)getPreferenceByKey(R.string.pref_client_endpoint);
		prefServer.setEnabled(TouchatagApplication.FULL);
		
		
		String[] serverNames = Server.getEntries();
		String[] serverUrls = Server.getEntryValues();
		
		prefServer.setEntries(serverNames);
		prefServer.setEntryValues(serverUrls);
		prefServer.setSummary(settingsStore.getServer().getName());
		prefServer.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Server server = Server.valueOf((String)newValue);
				settingsStore.setServer(server);
				preference.setSummary(server.getName());
				return true;
			}
		});
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_CLEAR_CREDENTIALS :
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to clear your username and password?") //
					.setCancelable(true) //
					.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							settingsStore.clearCredentials();
							startActivity(new Intent(SettingsActivity.this, CredentialsActivity.class));
							SettingsActivity.this.finish();
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).setTitle("Are you sure ?");
			return builder.create();
		case DIALOG_REVOKE_AUTHORIZATION :
			builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to revoke the app's permission to access your resources? You will need to re-authorize to use the app's full functionality.") //
					.setCancelable(true) //
					.setPositiveButton("Revoke", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							settingsStore.revokeAuthorization();
							startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
							SettingsActivity.this.finish();
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).setTitle("Are you sure ?");
			return builder.create();
		
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private Preference getPreferenceByKey(int key) {
		return getPreferenceByKey(getPreferenceScreen(), getString(key));
	}
	
	private Preference getPreferenceByKey(Preference pref, String key){
		if(pref instanceof PreferenceGroup){
			PreferenceGroup prefGroup = (PreferenceGroup)pref;
			int count = prefGroup.getPreferenceCount();
			for (int i = 0; i < count; i++) {
				Preference matchedPref = getPreferenceByKey(prefGroup.getPreference(i), key);
				if(matchedPref != null){
					return matchedPref;
				}
			}
		} else if(pref instanceof Preference){
			if (pref.getKey().equals(key)) {
				return pref;
			}
		}
		return null;
	}
}
