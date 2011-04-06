package com.touchatag.android.util;

import java.net.URLEncoder;

import android.net.Uri;
import android.util.Base64;

public class QrCodeGenerator {

	private static final String API_URL = "http://api.qrserver.com/v1/create-qr-code/?data=${data}&size=400x400";
	
	public static Uri getUri(String tagId){
		String data = "http://ttag.be/b?" + Base64.encodeToString(tagId.getBytes(), Base64.DEFAULT);
		String url = API_URL.replace("${data}", URLEncoder.encode(data));
		return Uri.parse(url);
	}
	
}
