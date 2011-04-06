package com.touchatag.android.client.rest.serialization;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.android.client.rest.model.ClaimingRule;
import com.touchatag.android.client.rest.model.Error;
import com.touchatag.android.client.rest.model.Tag;

public class TagDeserializer extends PageItemDeserializer<Tag>{

	private static final String TAG_TAG = "tag";
	
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_OWNERID = "ownerid";
	private static final String ATTR_IDENTIFIER = "identifier";
	private static final String ATTR_HASH = "hash";
	private static final String ATTR_DISABLED = "disabled";
	private static final String ATTR_CREATED = "created";
	private static final String ATTR_CLAIMINGRULE = "claimingrule";
	
	
//	    type="RFID"
//		ownerid="7ba990c3-bba4-4123-ac03-5220ba9ec615"
//		identifier="0x04D690193E2580"
//		hash="7b177b4e43e626b857be0d6c58b501ac"
//		disabled="false"
//		created="2011-02-12T14:56:26.000+01:00"
//		claimingrule="UNLOCKED"
	
	
	public Tag deserialize(XmlPullParser xpp) throws XmlPullParserException, IOException  {
		Tag tag = new Tag();
		if(xpp.getEventType() == XmlPullParser.START_TAG){
			tag.setType(getAttributeValue(xpp, ATTR_TYPE));
			tag.setOwnerId(getAttributeValue(xpp, ATTR_OWNERID));
			tag.setIdentifier(getAttributeValue(xpp, ATTR_IDENTIFIER));
			tag.setHash(getAttributeValue(xpp, ATTR_HASH));
			tag.setDisabled(Boolean.getBoolean(getAttributeValue(xpp, ATTR_DISABLED)));
			//tag.setCreated(new Date(Date.parse(getAttributeValue(xpp, ATTR_CREATED))));
			tag.setCreated(getAttributeValue(xpp, ATTR_CREATED));
			tag.setClaimingRule(ClaimingRule.valueOf(getAttributeValue(xpp, ATTR_CLAIMINGRULE)));
		}
		return tag;
		
	}
	
	public Tag deserialize(String tagXml){
		Tag tag = new Tag();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(tagXml));
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				if (eventType == XmlPullParser.START_TAG) {
					String startTag = xpp.getName();
					if(TAG_TAG.equals(startTag)){
						tag = deserialize(xpp);
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
		return tag;
	}
	
}
