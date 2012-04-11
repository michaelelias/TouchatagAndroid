package com.touchatag.android.correlation.api.v1_2.command;

public class InvalidCredentialsException extends Exception {

	public InvalidCredentialsException() {
		super("The supplied username or password are incorrect.");
	}
	
}
