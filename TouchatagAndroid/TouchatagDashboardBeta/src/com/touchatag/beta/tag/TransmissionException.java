package com.touchatag.beta.tag;

public class TransmissionException extends Exception {

	public TransmissionException(Throwable cause){
		super("Could not transceive data to tag", cause);
	}
	
}
