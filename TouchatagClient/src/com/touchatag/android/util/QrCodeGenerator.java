package com.touchatag.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class QrCodeGenerator {

	private static final String API_URL = "http://api.qrserver.com/v1/create-qr-code/?data=${data}&size=${size}x${size}";
	
	public static Uri getUri(String tagId, int size){
		String data = "http://ttag.be/b?" + Base64.encodeToString(tagId.getBytes(), Base64.DEFAULT);
		String url = API_URL.replace("${data}", URLEncoder.encode(data));
		url = url.replace("${size}", String.valueOf(size));
		return Uri.parse(url);
	}
	
	public static Bitmap getQrBitmap(String tagId, int size){
		Uri uri = getUri(tagId, size);
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(uri.toString()).openStream(), 1024);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 1024);

			byte[] b = new byte[1024];
			int read;
			while ((read = in.read(b)) != -1) {
				out.write(b, 0, read);
			}
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			
		} catch (IOException e) {
			Log.e("QRTagViewer", "Could not load Bitmap from: " + uri.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return bitmap;
	}
	
}
