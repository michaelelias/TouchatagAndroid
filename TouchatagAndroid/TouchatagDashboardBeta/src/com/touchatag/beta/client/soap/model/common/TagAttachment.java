package com.touchatag.beta.client.soap.model.common;

import java.io.Serializable;
import java.net.URI;

public class TagAttachment implements Serializable{

	private URI identifier;

	private String mimeType;

	private byte[] content;

	public URI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
