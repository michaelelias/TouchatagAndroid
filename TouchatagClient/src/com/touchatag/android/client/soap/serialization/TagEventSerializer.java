package com.touchatag.android.client.soap.serialization;

import android.util.Base64;

import com.touchatag.android.client.soap.model.request.TagEvent;
import com.touchatag.android.util.Utils;

public class TagEventSerializer {

	private static String TAGEVENT_WITH_ACTIONTAG = "<?xml version=\"1.0\" ?>\n<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n<S:Body>\n<ns2:handleTagEvent xmlns:ns3=\"http://www.touchatag.com/acs/api/correlation-1.1\" xmlns:ns2=\"http://www.touchatag.com/acs/api/correlation-1.2\" tagEventType=\"${tagEventType}\">\n<clientId>\n<id>${clientId}</id>\n<name>${clientName}</name>\n</clientId>\n<readerId>\n<uid>${readerUID}</uid>\n<serialNr>${readerSerialNr}</serialNr>\n</readerId>\n<actionTag>\n<tagId>${actionTagId}</tagId>\n<tagData>${actionTagData}</tagData>\n</actionTag>\n</ns2:handleTagEvent>\n</S:Body>\n</S:Envelope>";
	
	private static String PROP_TAG_EVENT_TYPE = "tagEventType";
	private static String PROP_CLIENT_ID = "clientId";
	private static String PROP_CLIENT_NAME = "clientName";
	private static String PROP_READER_UID = "readerUID";
	private static String PROP_READER_SERIALNR = "readerSerialNr";
	private static String PROP_ACTION_TAG_ID = "actionTagId";
	private static String PROP_ACTION_TAG_DATA = "actionTagData";
	
	
	public static String serialize(TagEvent tagEvent){
		String serializedTagEvent = null;
		if(tagEvent.getActionTag() != null){
			if(tagEvent.getContextTag() == null){
				serializedTagEvent = TAGEVENT_WITH_ACTIONTAG;
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_TAG_EVENT_TYPE, tagEvent.getTagEventType().name());
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_CLIENT_ID, tagEvent.getClientId().getId());
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_CLIENT_NAME, tagEvent.getClientId().getName());
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_READER_UID, Utils.toHexString(tagEvent.getReaderId().getUid()));
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_READER_SERIALNR, tagEvent.getReaderId().getSerialNr());
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_ACTION_TAG_ID, tagEvent.getActionTag().getTagId().getIdentifier());
				serializedTagEvent = replaceProperty(serializedTagEvent, PROP_ACTION_TAG_DATA, Base64.encodeToString(tagEvent.getActionTag().getTagData(), Base64.DEFAULT));
			}
		}
		return serializedTagEvent;
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
