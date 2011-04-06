package com.touchatag.android.client.soap.model.common;

public enum Role {

	USER("tiki:user"), ADMIN("tiki:admin");
	
	private String value;
	
	private Role(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
