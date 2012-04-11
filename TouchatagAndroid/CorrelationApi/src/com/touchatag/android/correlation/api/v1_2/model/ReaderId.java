package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ReaderId {

	@Element(required=false)
	private String uid;
	
	@Element
	private String readerId;

	public ReaderId(String readerId) {
		super();
		this.readerId = readerId;
	}
	
	public ReaderId(String readerId, String uid) {
		super();
		this.readerId = readerId;
		this.uid = uid;
	}



	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	
	
	
}
