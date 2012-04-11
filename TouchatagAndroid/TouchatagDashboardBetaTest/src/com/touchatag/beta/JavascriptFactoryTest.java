package com.touchatag.beta;

import org.junit.Test;

import com.touchatag.beta.activity.template.JavascriptFactory;

public class JavascriptFactoryTest {

	@Test
	public void test(){
		String templateId = "foursquareTemplate";
		String venueId = "venueId";
		String shout = "hello world";
		String latitude = "40.1";
		String longitude = "40.1";
		
		String js = JavascriptFactory.createFoursquareCheckinScript(templateId, venueId, shout, latitude, longitude);
		System.out.println(js);
	}
	
}
