package com.touchatag.android.client.rest.serialization;

import java.util.Map;
import java.util.Map.Entry;

public class AdapterUtils {

	public static final String startTag(String tagName){
		return "<" + tagName + ">";
	}
	
	public static final String endTag(String tagName){
		return "</" + tagName + ">";
	}
	
	public static final String startTagWithAttributes(String tagName, Map<String, String> attributes){
		return startTagWithAttributes(tagName, attributes, false);
	}
	
	public static final String startTagWithAttributes(String tagName, Map<String, String> attributes, boolean closed){
		StringBuilder sb = new StringBuilder();
		sb.append("<" + tagName + " ");
		for(Entry<String, String> entry : attributes.entrySet()){
			sb.append(entry.getKey() + "=\"" + entry.getValue() + "\" ");
		}
		sb.append((closed ? "/" : "") + ">");
		return sb.toString();
	}
	
}
