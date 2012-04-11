package com.touchatag.beta;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.touchatag.acs.api.client.model.specification.Specification;
import com.touchatag.beta.activity.template.SpecificationFactory;

public class SpecificationFactoryTest {

	private static String XML_PREFIX = "<?xml version=\"1.0\" ?>";
	
	@Test
	public void testFoursquareCheckinSpecification(){
		String uri = "http://m.foursquare.com/venue/12345";
		String javascript = "var = 'test';";
		
		Map<String, String> params = new HashMap<String, String>();
		Specification spec = SpecificationFactory.createWebLinkSpecWithJavascript(uri, javascript, params);

		String xml = toXml(spec);
		System.out.println(xml);
	}
	
	@Test
	public void testWeblinkSpecification(){
		String uri = "http://m.foursquare.com/venue/12345";
		Specification spec = SpecificationFactory.createSimpleWebLinkSpec(uri);
		String xml = toXml(spec);
		System.out.println(xml);
	}
	
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
