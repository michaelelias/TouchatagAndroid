package com.touchatag.beta.client.rest.serialization;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class AdapterUtils {

	private static String XML_PREFIX = "<?xml version=\"1.0\" ?>";
	
	public static final String startTag(String tagName){
		return "<" + tagName + ">";
	}
	
	public static final String endTag(String tagName){
		return "</" + tagName + ">";
	}
	
	public static final String singleTag(String tagName){
		return "<" + tagName + "/>";
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
	
	public static <T> String toXml(T object){
		try {
			Serializer serializer = new Persister();
			StringWriter writer = new StringWriter();
			serializer.write(object, writer);
			return XML_PREFIX + writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T fromXml(String xml, Class<T> clazz){
		Serializer serializer = new Persister();
		try {
			return serializer.read(clazz, xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
