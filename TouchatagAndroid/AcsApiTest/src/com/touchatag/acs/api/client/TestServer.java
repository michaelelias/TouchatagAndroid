package com.touchatag.acs.api.client;

import com.touchatag.android.correlation.api.v1_2.CorrelationServer;

public class TestServer implements AcsServer, CorrelationServer {

	private String KEY = "edce-6495-219b-4252-afeb-9ca0-3627-258f";
	private String SECRET = "GNS4h96SjLBfKKBNeY1yrvT5RHze0Kyc";
	private String ACCESS_TOKEN = "892a0ef6117f66c8aa67143f219ffbdc";
	private String ACCESS_TOKEN_SECRET = "1ace3dabad7343f51370808eae8ca710";

	@Override
	public String getName() {
		return "ACS Test Server";
	}

	@Override
	public String getUrl() {
		return "http://localhost:8080";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getSecret() {
		return SECRET;
	}

	public String getAccessToken() {
		return ACCESS_TOKEN;
	}

	public String getAccessTokenSecret() {
		return ACCESS_TOKEN_SECRET;
	}
}
