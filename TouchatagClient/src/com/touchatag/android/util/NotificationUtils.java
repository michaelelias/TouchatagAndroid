package com.touchatag.android.util;

import android.content.Context;
import android.text.Spanned;
import android.widget.Toast;

public class NotificationUtils {

	public static void showFeedbackMessage(Context ctx, String message){
		Toast toast = Toast.makeText(ctx.getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void showFeedbackMessage(Context ctx, Spanned message){
		Toast toast = Toast.makeText(ctx.getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
}
