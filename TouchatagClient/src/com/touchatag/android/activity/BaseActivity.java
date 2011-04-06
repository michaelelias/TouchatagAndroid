package com.touchatag.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.touchatag.android.R;

public abstract class BaseActivity extends Activity {

	private static final int DIALOG_ABOUT = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ABOUT){
			String message = "This app was made by @MichaelElias88. \n\nIf you have any feedback, requests or complaints, please let me know!. \n\nHappy tagging!";
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About") //
				   .setIcon(android.R.drawable.ic_dialog_info) //
				   .setMessage(message) //
			       .setCancelable(false) //
			       .setPositiveButton("Send Feedback", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Intent intent = new Intent(android.content.Intent.ACTION_SEND);  
			        	   intent.setType("plain/text");  
			        	   intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"touchatag_android@gmail.com"});  
			        	   intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback on Touchatag's Android App");  
			        	   startActivity(intent);
			           }
			       }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_settings:
			startActivity(new Intent(getBaseContext(), SettingsActivity.class));
			return true;
		case R.id.menuitem_about :
			showDialog(DIALOG_ABOUT);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
