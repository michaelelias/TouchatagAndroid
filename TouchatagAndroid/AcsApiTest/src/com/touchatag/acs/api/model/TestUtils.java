package com.touchatag.acs.api.model;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class TestUtils {

	private static String XML_PREFIX = "<?xml version=\"1.0\" ?>";
	
	public static <T> String toXml(T object) {
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

	public static <T> T fromXml(String xml, Class<T> clazz) {
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
