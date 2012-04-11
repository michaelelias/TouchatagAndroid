package com.touchatag.beta.client.soap.serialization;

import java.util.List;

import com.touchatag.beta.client.soap.model.common.Role;
import com.touchatag.beta.client.soap.model.common.UserDTO;
import com.touchatag.beta.client.soap.model.request.CreateUser;
import com.touchatag.beta.util.HexFormatter;

public class CreateUserSerializer {

	private static String CREATE_USER = "<?xml version=\"1.0\" ?>\n<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n<S:Body>\n<ns1:createUser xmlns:ns1=\"http://www.tikitag.com\">\n<userName>${username}</userName>\n<password>${password}</password>\n<role>${role}</role>\n</ns1:createUser>\n</S:Body>\n</S:Envelope>";
	
	private static String PROP_USERNAME = "username";
	private static String PROP_PASSWORD = "password";
	private static String PROP_ROLE = "role";
	
	public static String serialize(CreateUser createUser){
		String serializedCreateUser = CREATE_USER;
		UserDTO user = createUser.getUser();
		serializedCreateUser = replaceProperty(serializedCreateUser, PROP_USERNAME, user.getUsername());
		serializedCreateUser = replaceProperty(serializedCreateUser, PROP_PASSWORD, encodePassword(user.getPassword()));
		List<Role> roles = user.getRoles();
		String rolesString = "";
		for(int i = 0; i < roles.size(); i++){
			rolesString += roles.get(i).getValue();
			if(i < roles.size() - 1){
				rolesString += ",";
			}
		}
		serializedCreateUser = replaceProperty(serializedCreateUser, PROP_ROLE, rolesString);
		return serializedCreateUser;
	}
	
	private static String encodePassword(byte[] password){
		String hexEncoded = HexFormatter.toHexString(password);
		return android.util.Base64.encodeToString(hexEncoded.getBytes(), android.util.Base64.DEFAULT);
	}
	
	private static String replaceProperty(String source, String prop, String value){
		if(value == null){
			value = "";
		}
		return source.replace("${" + prop + "}", value);
	}
	
	private static String replaceProperty(String source, String prop, byte[] value){
		String stringValue = "";
		if(value != null){
			stringValue = new String(value);
		}
		return replaceProperty(source, prop, stringValue);
	}
	
}
