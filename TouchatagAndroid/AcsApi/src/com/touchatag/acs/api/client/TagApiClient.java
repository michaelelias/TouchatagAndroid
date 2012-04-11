package com.touchatag.acs.api.client;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.touchatag.acs.api.client.model.ClaimingRule;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.TagPage;
import com.touchatag.acs.api.client.model.TagType;

public abstract class TagApiClient extends BaseAcsApiClient {

	public TagApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public Tag acquire(String forIdentityId, String atIdentityId, String onClientName, int timeout) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/acquirements/rfid-tag/for/" + forIdentityId + "/at/" + atIdentityId + "/on/" + onClientName + "?timeout=" + timeout, Tag.class);
	}

	public Tag acquire(String forIdentityId, String atIdentityId, int timeout) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/acquirements/rfid-tag/for/" + forIdentityId + "/at/" + atIdentityId + "?timeout=" + timeout, Tag.class);
	}

	public boolean cancelAcquire(String atIdentityId, String onClientName) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/acquirements/rfid-tag/at/" + atIdentityId + "/on/" + onClientName);
	}

	public boolean cancelAcquireAt(String atIdentityId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/acquirements/rfid-tag/at/" + atIdentityId);
	}

	public boolean cancelAcquireOn(String clientName) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/acquirements/rfid-tag/on/" + clientName);
	}

	public boolean cancelAcquire() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/acquirements/rfid-tag");
	}

	public boolean relinquish(String tagHash) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/tags/" + tagHash);
	}

	public Tag getByHash(String tagHash) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/tags/" + tagHash, Tag.class);
	}

	public Tag getByShortCode(String shortCode) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/tags/?shortCode=" + shortCode, Tag.class);
	}

	public TagPage getPage(int pageNumber, int pageSize) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/tags/page/" + pageNumber + "?pageSize=" + pageSize, TagPage.class);
	}

	public ClaimingRule setClaimingRule(String tagHash, ClaimingRule claimingRule) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		HttpPut httpPut = new HttpPut(baseURI + "/tags/" + tagHash + "/claimingrule");
		try {
			ByteArrayEntity entity = new ByteArrayEntity(claimingRule.name().getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPut.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		HttpResponse response = connect(httpPut);
		processResponse(response, 200);
		String body = getResponseBody(response);
		return ClaimingRule.valueOf(body);
	}

	public void enable(String tagHash) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		doPut("/tags/" + tagHash + "/enable");
	}

	public void disable(String tagHash) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		doPut("/tags/" + tagHash + "/disable");
	}

	public ClaimingRule getClaimingRule(String tagHash) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/tags/" + tagHash + "/claimingrule", ClaimingRule.class);
	}

	public Tag generateQRTag() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPost("/tags?type=" + TagType.QR, Tag.class);
	}

}
