package com.touchatag.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class ActivityUtils {

	public static AlertDialog getAboutDialog(final Activity activity){
		String message = "This app was made by @MichaelElias88. \n\nIf you have any feedback, requests or complaints, please let me know!. \n\nHappy tagging!";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
		        	   activity.startActivity(intent);
		           }
		       }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		return builder.create();
	}
	
}
