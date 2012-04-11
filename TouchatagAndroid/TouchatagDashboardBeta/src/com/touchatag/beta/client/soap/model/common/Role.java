package com.touchatag.beta.client.soap.model.common;

public enum Role {

	USER("tiki:user"), ADMIN("tiki:admin"), USER_MANAGER("user:manager"), BLOCK_EXTENSION("block:extension");
	
	private String value;
	
	public static Role resolve(String value){
		for(Role role : values()){
			if(role.getValue().equals(value)){
				return role;
			}
		}
		return null;
	}
	
	private Role(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
