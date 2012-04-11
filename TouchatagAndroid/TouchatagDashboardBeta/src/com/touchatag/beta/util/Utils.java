package com.touchatag.beta.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder("0x");
		for (byte b : bytes) {
			String fragment = Integer.toHexString(b & 0xFF).toUpperCase();
			if (fragment.length() == 1) {
				sb.append("0");
			}
			sb.append(fragment);
		}
		return sb.toString();

	}

	public static byte[] md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			return digest.digest();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encodeAmpersant(String uri){
		return uri.replace("&", "%26");
	}
	
	public static String decodeAmpersant(String encodedURI){
		return encodedURI.replace("%26", "&");
	}

}
