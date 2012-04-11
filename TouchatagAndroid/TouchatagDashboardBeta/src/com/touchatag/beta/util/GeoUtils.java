package com.touchatag.beta.util;

public class GeoUtils {

	public static Double toDouble(int coordinateE6){
		return ((Double) (((double) coordinateE6) / 1000000));
	}
	
	public static Integer toIntE6(double coordinateDouble){
		return (int)(coordinateDouble * 1000000);
	}
}
