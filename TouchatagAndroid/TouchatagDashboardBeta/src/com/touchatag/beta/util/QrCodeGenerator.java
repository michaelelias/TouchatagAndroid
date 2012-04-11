package com.touchatag.beta.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class QrCodeGenerator {

	private static final String TAG = QrCodeGenerator.class.getSimpleName();
	private static final String API_URL = "http://api.qrserver.com/v1/create-qr-code/?data=${data}&size=400x400";

	public static Uri getUri(String tagId) {
		tagId = tagId.substring(2);
		String data = "http://ttag.be/m/" + tagId;
		String url = API_URL.replace("${data}", URLEncoder.encode(data));
		return Uri.parse(url);
	}

	public static Uri getCachedBitmapUri(Context ctx, String tagId) {
		try {
			try {
				FileInputStream fis = getFileInputStream(ctx, tagId);
			} catch (FileNotFoundException e) {
				Bitmap qrcodeBitmap = fetchBitmap(ctx, tagId);
				storeBitmap(ctx, tagId, qrcodeBitmap);
			}
			File file = ctx.getFileStreamPath(tagId + ".jpg");
			return Uri.fromFile(file);
		} catch (IOException e) {
			Log.e(TAG, "IO Exception when trying to save QR code bitmap");
			return null;
		}
	}

	private static FileOutputStream getFileOutputStream(Context ctx, String tagId) throws FileNotFoundException {
		return ctx.openFileOutput(tagId + ".jpg", Context.MODE_WORLD_READABLE);
	}

	private static FileInputStream getFileInputStream(Context ctx, String tagId) throws FileNotFoundException {
		return ctx.openFileInput(tagId + ".jpg");
	}

	private static void storeBitmap(Context ctx, String tagId, Bitmap qrcodeBitmap) throws IOException {
		FileOutputStream fos = getFileOutputStream(ctx, tagId);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		qrcodeBitmap.compress(CompressFormat.JPEG, 50, bos);
		bos.flush();
		bos.close();
	}

	private static boolean isCacheDirAvailable() {
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			externalStorageAvailable = externalStorageWriteable = false;
		}
		return externalStorageAvailable && externalStorageWriteable;
	}

	public static Bitmap fetchBitmap(Context ctx, String tagId) {
		try {
			FileInputStream fis = getFileInputStream(ctx, tagId);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e1) {
			String url = getUri(tagId).toString();
			Bitmap bitmap = null;
			InputStream in = null;
			BufferedOutputStream out = null;
			try {
				in = new BufferedInputStream(new URL(url).openStream(), 1024);
				
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
				storeBitmap(ctx, tagId, bitmap);
			} catch (IOException e) {
				Log.e("QRTagViewer", "Could not load Bitmap from: " + url);
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

}
