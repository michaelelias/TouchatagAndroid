package com.touchatag.acs.api.client;

import com.touchatag.acs.api.client.model.AcsIdentity;

public abstract class AcsIdentityApiClient extends BaseAcsApiClient {

	public AcsIdentityApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public AcsIdentity get(String identityId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doGet("/acsidentities/" + identityId, AcsIdentity.class);
	}
	
	public AcsIdentity update(AcsIdentity identity) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doPut("/acsidentities/" + identity.getIdentityId(), identity, AcsIdentity.class);
	}
}
