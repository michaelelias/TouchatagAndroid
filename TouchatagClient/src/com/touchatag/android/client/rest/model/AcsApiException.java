package com.touchatag.android.client.rest.model;

public class AcsApiException extends Exception {

	private Error error;
	
	public AcsApiException(Error error){
		this.error = error;
	}
	
	public Error getError(){
		return error;
	}

	@Override
	public String getMessage() {
		return error.getMessage();
	}
	
	
}
