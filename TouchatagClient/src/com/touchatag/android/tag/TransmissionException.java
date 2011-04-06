package com.touchatag.android.tag;

public class TransmissionException extends Exception {

	public TransmissionException(Throwable cause){
		super("Could not transceive data to tag", cause);
	}
	
}
