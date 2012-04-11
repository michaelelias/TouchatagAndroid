package com.touchatag.beta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.beta.R;
import com.touchatag.beta.util.QrCodeGenerator;

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
		new GenerateQrCodeAsyncTask().execute(tagId);
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
			return QrCodeGenerator.fetchBitmap(ViewQrCodeActivity.this, params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			imgQr.setImageBitmap(result);
		}
		
		

	}

}
