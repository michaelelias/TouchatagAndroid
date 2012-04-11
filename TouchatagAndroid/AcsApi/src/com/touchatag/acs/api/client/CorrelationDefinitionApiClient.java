package com.touchatag.acs.api.client;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;

public abstract class CorrelationDefinitionApiClient extends BaseAcsApiClient {

	public CorrelationDefinitionApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public CorrelationDefinition get() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doGet("/correlationDefinition", CorrelationDefinition.class);
	}
	
	public CorrelationDefinition update(CorrelationDefinition corrDef) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		HttpPut httpPut = new HttpPut(baseURI + "/correlationDefinition");
		String xml = toXml(corrDef);
		// Very ugly hack for getting around Simpl Xml's namespace handling failure...
		xml = xml.replaceAll("<asso", "<ns2:asso");
		try {
			ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes(ENCODING));
			entity.setContentEncoding(ENCODING);
			entity.setContentType("text/xml");
			httpPut.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		HttpResponse response = connect(httpPut);
		processResponse(response, 200);
		String body = getResponseBody(response);
		// Very ugly hack for getting around Simpl Xml's namespace handling failure...
		body = body.replaceAll("<ns2:asso ", "<asso ");
		return fromXml(body, CorrelationDefinition.class);
	}

}
