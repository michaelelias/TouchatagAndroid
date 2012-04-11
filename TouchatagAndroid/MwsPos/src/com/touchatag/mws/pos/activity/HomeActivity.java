package com.touchatag.mws.pos.activity;

import android.app.Activity;
import android.os.Bundle;

import com.touchatag.mws.api.client.AffiliateApiClient;
import com.touchatag.mws.pos.R;
import com.touchatag.mws.pos.config.Server;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		//AffiliateApiClient client = new AffiliateApiClient(Server.BETA, );
		
	}

	
	
}
