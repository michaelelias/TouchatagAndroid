package com.touchatag.beta.client.soap.serialization;

import com.touchatag.beta.client.soap.model.request.GetUser;

public class GetUserSerializer {

private static String GET_USER = "<?xml version=\"1.0\" ?>\n<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n<S:Body>\n<ns1:getUser xmlns:ns1=\"http://www.tikitag.com\">\n${username}\n</ns1:getUser>\n</S:Body>\n</S:Envelope>";
	
	private static String PROP_USERNAME = "username";
	
	public static String serialize(GetUser getUser){
		String serializedGetUser = GET_USER;
		String username = getUser.getUsername();
		serializedGetUser = replaceProperty(serializedGetUser, PROP_USERNAME, username);
		return serializedGetUser;
	}
	
	private static String replaceProperty(String source, String prop, String value){
		if(value == null){
			value = "";
		}
		return source.replace("${" + prop + "}", value);
	}
	
}
