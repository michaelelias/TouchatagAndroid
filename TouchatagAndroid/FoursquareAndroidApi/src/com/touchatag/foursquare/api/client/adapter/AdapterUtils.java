package com.touchatag.foursquare.api.client.adapter;

import java.lang.reflect.Method;

import org.json.JSONObject;

public class AdapterUtils {

	public static <TARGET, SOURCE extends JSONObject> Mapping<TARGET, SOURCE> mapping(TARGET target, SOURCE source){
		return new Mapping<TARGET, SOURCE>(target, source);
	}
	
	/**
	 * Attempts to map a field in the given json object to the field in the given pojo object.
	 * If the field is not present in the json object, no action is performed.
	 * If the field is not present in the pojo, an exception is raised
	 * The method will look for a setter method in java beans notation for the given pojo field name.
	 * If a setter is found it will be used to set the field. Otherwise the field is set through reflection.
	 * 
	 * @param <T>
	 * @param pojo
	 * @param pojoFieldName
	 * @param jsonObject
	 * @param jsonFieldName
	 * @param required
	 */
	public static class Mapping<TARGET, SOURCE extends JSONObject> {
		
		private TARGET target;
		private SOURCE source;
		
		private Mapping(TARGET pojo, SOURCE source){
			this.target = pojo;
			this.source = source;
		}
		
		public void setSource(SOURCE source){
			this.source = source;
		}
		
		public void map(String pojoFieldName, String jsonFieldName){
			map(pojoFieldName, jsonFieldName, true);
		}
		
		public void map(String pojoFieldName, String jsonFieldName, boolean required){
			Method method = getSetterMethod(target, pojoFieldName);
			if(method != null){
				if(source.has(jsonFieldName)){
					try {
						method.invoke(target, source.getString(jsonFieldName));
					} catch (Exception e) {
						throw new RuntimeException(e);
					} 
				} else if(required){
					throw new RuntimeException("A field '" + jsonFieldName + "' is not present in the given JSONObject and is indicated as required.");
				}
			}
		}
		
		private <T> Method getSetterMethod(T pojo, String pojoFieldName){
			pojoFieldName = pojoFieldName.substring(0, 1).toUpperCase() + pojoFieldName.substring(1);
			String expectedMethodName = "set" + (pojoFieldName);
			Method[] methods = pojo.getClass().getMethods();
			for(Method method : methods){
				if(method.getName().equals(expectedMethodName)){
					return method;
				}
			}
			return null;
		}
	}
	
}
