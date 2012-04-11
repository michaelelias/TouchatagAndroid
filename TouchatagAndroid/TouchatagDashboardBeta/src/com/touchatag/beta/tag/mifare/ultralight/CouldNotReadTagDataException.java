package com.touchatag.beta.tag.mifare.ultralight;

public class CouldNotReadTagDataException extends Exception {

	public CouldNotReadTagDataException(Throwable cause){
		super("Could not read data from tag", cause);
	}
}
