package com.touchatag.beta.client.soap.model.common;

import java.io.Serializable;

public class ReaderId implements Serializable{

	private byte[] uid;
	private String serialNr;
	
	public ReaderId(byte[] uid, String serialNr) {
		super();
		this.uid = uid;
		this.serialNr = serialNr;
	}
	public byte[] getUid() {
		return uid;
	}
	public void setUid(byte[] uid) {
		this.uid = uid;
	}
	public String getSerialNr() {
		return serialNr;
	}
	public void setSerialNr(String serialNr) {
		this.serialNr = serialNr;
	}
	
	
	
}
