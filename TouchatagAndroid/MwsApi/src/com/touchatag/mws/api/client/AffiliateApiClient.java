package com.touchatag.mws.api.client;

import oauth.signpost.OAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.touchatag.mws.api.client.adapter.AffiliateAdapter;
import com.touchatag.mws.api.client.model.Affiliate;

public class AffiliateApiClient extends BaseMwsClient {

	protected AffiliateApiClient(MwsServer server, OAuthConsumer consumer, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public Affiliate getAffiliate(String organizationId, String affiliateId) throws Exception {
		HttpGet httpGet = new HttpGet(baseURI + "/organizations/" + organizationId + "/affiliates/" + affiliateId);
		HttpResponse response = connect(httpGet);
		processResponse(response, 200);
		String responseBody = getResonseBody(response);
		return AffiliateAdapter.fromXml(responseBody);
	}

}
