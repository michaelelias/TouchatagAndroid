package com.touchatag.acs.api.client.model.specification;

public enum PropertyType {

	URI("uri"),
	TEXT("text"),
	INTEGER("integer"),
	DELEGATE("delegate");
	
	private String type;
	
	private PropertyType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
}
