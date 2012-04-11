package com.touchatag.android.correlation.api.v1_2;

import com.touchatag.android.correlation.api.v1_2.CorrelationServer;

public class TestServer implements CorrelationServer {

	@Override
	public String getName() {
		return "Correlation Local Test Server";
	}

	@Override
	public String getUrl() {
		return "http://localhost:8080";
	}

}
