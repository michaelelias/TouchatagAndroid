package com.touchatag.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.touchatag.android.R;

public class LiteHomeActivity extends BaseActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.lite_home);
		
		StringBuilder sb = new StringBuilder();
		sb.append("You logged in and now what? Well you start scanning Touchatag tags!");
		sb.append("<br/><br/>");
		sb.append("To scan a tag you wave your phone over a tag. ");
		sb.append("The Touchatag app will then send the tag info to our server which returns the action associated to that tag. ");
		sb.append("<br/><br/>");
		sb.append("You can associate actions to a tag using the <a href=\"http://www.touchatag.com/dashboard\">dashboard</a> on our site. ");
		sb.append("<br/><br/>");
		sb.append("When associating a tag to your app in the dashboard, use your phone. The service will then associate the tag to your app.");
		sb.append("<br/><br/>");
		sb.append("You can buy tags in our <a href=\"http://www.touchatag.com/e-store\">e-store</a>");
		TextView txtHowto = (TextView)findViewById(R.id.lbl_litehome_howto);
		txtHowto.setMovementMethod(LinkMovementMethod.getInstance());
		txtHowto.setText(Html.fromHtml(sb.toString()));
		
		Button btnEStore = (Button)findViewById(R.id.btn_litehome_estore);
		btnEStore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.touchatag.com/e-store"));
				startActivity(intent);
			}
		});
		
		Button btnTutorial = (Button)findViewById(R.id.btn_litehome_tutorial);
		btnTutorial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.touchatag.com/help"));
				startActivity(intent);
			}
		});
	}
	
}
