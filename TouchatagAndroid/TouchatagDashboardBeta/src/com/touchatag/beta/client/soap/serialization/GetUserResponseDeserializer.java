package com.touchatag.beta.client.soap.serialization;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.beta.client.soap.model.common.UserDTO;
import com.touchatag.beta.client.soap.model.response.GetUserResponse;

public class GetUserResponseDeserializer {

	private static final String TAG_GET_USER_RESPONSE = "getUserResponse";
	private static final String TAG_USERNAME = "userName";
	private static final String TAG_ROLE = "applicationResponse";
	private static final String TAG_EMAIL = "email";
	
	public static GetUserResponse deserialize(String response){
		
		UserDTO user = new UserDTO();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(response));
			int eventType = xpp.getEventType();

			boolean inGetUserResponse = false;
			boolean inUsername = false;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					String startTag = xpp.getName();
					if (TAG_GET_USER_RESPONSE.equalsIgnoreCase(startTag)) {
						inGetUserResponse = true;
					} else if (inGetUserResponse && TAG_USERNAME.equalsIgnoreCase(startTag)) {
						inUsername = true;
					} 
				} else if (eventType == XmlPullParser.END_TAG) {
					if (inGetUserResponse && TAG_GET_USER_RESPONSE.equalsIgnoreCase(xpp.getName())) {
						inGetUserResponse = false;
					} else if (inUsername && TAG_USERNAME.equalsIgnoreCase(xpp.getName())) {
						inUsername = false;
					} 
				} else if (eventType == XmlPullParser.TEXT) {
					if (inUsername) {
						user.setUsername(xpp.getText());
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		GetUserResponse getUserResponse = new GetUserResponse();
		getUserResponse.setUser(user);
		return getUserResponse;
	}
	
}
