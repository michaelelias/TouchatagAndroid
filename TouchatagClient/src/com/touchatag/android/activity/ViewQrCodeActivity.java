package com.touchatag.android.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.utils.URLEncodedUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.touchatag.android.R;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.util.QrCodeGenerator;

public class ViewQrCodeActivity extends Activity {

	private static final String EXTRA_TAGID = "tag.id";
	private ImageView imgQr;

	public static Intent getViewQrTagIntent(Context ctx, Tag tag) {
		Intent intent = new Intent(ctx, ViewQrCodeActivity.class);
		intent.putExtra(EXTRA_TAGID, tag.getIdentifier());
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - View QR Tag");
		setContentView(R.layout.view_qr);
		imgQr = (ImageView) findViewById(R.id.img_qrview);
		
		String tagId = getIntent().getStringExtra(EXTRA_TAGID);
		new GenerateQrCodeAsyncTask().execute(QrCodeGenerator.getUri(tagId).toString());
	}

	private class GenerateQrCodeAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(ViewQrCodeActivity.this, null, "Generating Image...");
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
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

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			imgQr.setImageBitmap(result);
		}
		
		

	}

}
