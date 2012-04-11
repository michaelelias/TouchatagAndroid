package com.touchatag.acs.api.client;

import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.client.model.ApplicationPage;

public abstract class ApplicationApiClient extends BaseAcsApiClient {

	public ApplicationApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public Application get(String appId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/applications/" + appId, Application.class);
	}
	
	public Application create(Application app) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPost("/applications", app, Application.class);
	}
	
	public Application update(Application app) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/applications/" + app.getId(), app, Application.class);
	}
	
	public boolean delete(String appId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/applications/" + appId);
	}
	
	public ApplicationPage getPage(int pageNumber, int pageSize) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doGet("/applications/page/" + pageNumber + "?pageSize=" + pageSize, ApplicationPage.class);
	}
	
}
