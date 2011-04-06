package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;

public class Property implements Serializable {

	public PropertyType type;
	public Object value;
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<property");
		if(type != null){
			sb.append(" name=\"" + type.name() + "\"");
		}
		if(type != null){
			sb.append(">");
			sb.append(type.valueToXml(value));
			sb.append("</property>");
		} else {
			sb.append("/>");
		}
		return sb.toString();
	}
	
	public enum PropertyType {
		TEXT("text") {
			@Override
			public boolean validateValue(Object value) {
				return value instanceof String;
			}

			@Override
			public String valueToXml(Object value) {
				return "<text>" + value + "</text>";
			}
		}, 
		URI("uri") {
			@Override
			public boolean validateValue(Object value) {
				return value instanceof String;
			}

			@Override
			public String valueToXml(Object value) {
				return "<uri>" + value + "</uri>";
			}
		};
		
		private String nodeName;
		
		private PropertyType(String nodeName){
			this.nodeName = nodeName;
		}
		
		public String getNodeName(){
			return nodeName;
		}
		
		public abstract boolean validateValue(Object value);
		public abstract String valueToXml(Object value);
		
	}
}
