package com.touchatag.android.client.rest.serialization;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ErrorDeserializer {

	public static final String TAG_ERROR = "error";
	public static final String ATTR_MESSAGE = "message";
	public static final String ATTR_CODE = "errorCode";
	
	
	//<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ns2:error xmlns:ns2="http://acs.touchatag.com/schema/error-1.0" message="Tag detection failure." errorCode="4302"/>
	
	
	public static com.touchatag.android.client.rest.model.Error deserialize(String errorXml) {
		com.touchatag.android.client.rest.model.Error error = new com.touchatag.android.client.rest.model.Error();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(errorXml));
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				if (eventType == XmlPullParser.START_TAG) {
					String startTag = xpp.getName();
					if(TAG_ERROR.equals(startTag)){
						error.setMessage(xpp.getAttributeValue(0));
						error.setCode(Integer.parseInt(xpp.getAttributeValue(1)));
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
		return error;
	}
}
