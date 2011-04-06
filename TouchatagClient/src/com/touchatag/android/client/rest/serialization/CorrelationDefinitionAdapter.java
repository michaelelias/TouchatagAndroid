package com.touchatag.android.client.rest.serialization;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.android.client.rest.model.Association;
import com.touchatag.android.client.rest.model.CorrelationDefinition;

public class CorrelationDefinitionAdapter {

	private static final String ATTR_OWNERID = "ownerid";
	private static final String ATTR_COMMAND = "command";
	private static final String ATTR_TAGID = "tagid";
	private static final String TAG_CORRELATION_DEFINITION = "correlationDefinition";
	private static final String TAG_ASSOCIATIONS = "associations";
	private static final String TAG_ASSOCIATION = "asso";
	
	public String serialize(CorrelationDefinition corrDef){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("xmlns", "http://acs.touchatag.com/schema/associations-1.0");
		attributes.put("xmlns:ns2", "http://acs.touchatag.com/schema/ruleset-1.0");
		attributes.put("xmlns:ns3", "http://acs.touchatag.com/schema/correlationDefinition-1.0");
		attributes.put(ATTR_OWNERID, corrDef.getOwnerId());
		sb.append(AdapterUtils.startTagWithAttributes("ns3:" + TAG_CORRELATION_DEFINITION, attributes));
		sb.append(AdapterUtils.startTag(TAG_ASSOCIATIONS));
		
		for(Association asso : corrDef.getAssociations()){
			attributes = new HashMap<String, String>();
			attributes.put(ATTR_COMMAND, asso.getCommand());
			attributes.put(ATTR_TAGID, asso.getTagId());
			sb.append(AdapterUtils.startTagWithAttributes(TAG_ASSOCIATION, attributes, true));
		}
		
		sb.append(AdapterUtils.endTag(TAG_ASSOCIATIONS));
		sb.append(AdapterUtils.endTag("ns3:" + TAG_CORRELATION_DEFINITION));
		
		return sb.toString();
	}
	
	public CorrelationDefinition deserialize(String corrDefXml) {
		CorrelationDefinition corrDef = new CorrelationDefinition();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(corrDefXml));
			int eventType = xpp.getEventType();
			
			boolean inCorrelationdefinition = false;
			boolean inAssociations = false;
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				switch(eventType){
				case  XmlPullParser.START_TAG :
					String startTag = xpp.getName();
					if(TAG_CORRELATION_DEFINITION.equals(startTag)){
						inCorrelationdefinition = true;
						corrDef.setOwnerId(xpp.getAttributeValue(0));
					} else if(inCorrelationdefinition && TAG_ASSOCIATIONS.equals(startTag)){
						inAssociations = true;
					} else if(inAssociations && TAG_ASSOCIATION.equals(startTag)){
						Association asso = new Association();
						asso.setTagId(xpp.getAttributeValue(0));
						asso.setCommand(xpp.getAttributeValue(1));
						corrDef.getAssociations().add(asso);
					}
					break;
				case XmlPullParser.END_TAG :
					String endTag = xpp.getName();
					if(TAG_CORRELATION_DEFINITION.equals(endTag)){
						inCorrelationdefinition = false;
					} else if(inCorrelationdefinition && TAG_ASSOCIATIONS.equals(endTag)){
						inAssociations = false;
					} 
					break;
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
		return corrDef;
	}
}
