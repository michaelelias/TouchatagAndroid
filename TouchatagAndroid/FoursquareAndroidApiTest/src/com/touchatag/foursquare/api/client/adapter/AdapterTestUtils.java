package com.touchatag.foursquare.api.client.adapter;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class AdapterTestUtils {

	public static String readFile(String fileName) {
		try {
			InputStream is = AdapterTestUtils.class.getResourceAsStream(fileName);
			return IOUtils.toString(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
