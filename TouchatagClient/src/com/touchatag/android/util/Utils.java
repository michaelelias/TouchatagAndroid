package com.touchatag.android.util;

public class Utils {

	public static String toHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder("0x");
		for(byte b : bytes){
			String fragment = Integer.toHexString(b & 0xFF).toUpperCase();
			if(fragment.length() == 1){
				sb.append("0");
			}
			sb.append(fragment);
		}
		return sb.toString();
		
	}
	
}
