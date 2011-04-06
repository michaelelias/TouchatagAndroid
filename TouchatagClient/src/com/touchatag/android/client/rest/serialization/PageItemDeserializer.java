package com.touchatag.android.client.rest.serialization;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class PageItemDeserializer<T>{
	
	public abstract T deserialize(XmlPullParser xpp) throws XmlPullParserException, IOException;
	
	protected boolean isAtPageEndTag(XmlPullParser xpp) throws XmlPullParserException{
		return xpp.getEventType() == XmlPullParser.END_TAG && PageDeserializer.TAG_PAGE.equalsIgnoreCase(xpp.getName());
	}
	
	protected String getAttributeValue(XmlPullParser xpp, String attrName){
		int count = xpp.getAttributeCount();
		for(int i = 0; i < count; i++){
			if(xpp.getAttributeName(i).equals(attrName)){
				return xpp.getAttributeValue(i);
			}
		}
		return null;
	}
}
