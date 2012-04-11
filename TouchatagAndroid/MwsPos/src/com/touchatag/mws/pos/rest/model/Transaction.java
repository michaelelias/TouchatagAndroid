package com.touchatag.mws.pos.rest.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Transaction {

	public enum TransactionStatus {
		SUCCESS, REFUSED, RESERVED, FAILED, EXPIRED, CANCELLED, PROCESSED_DUAL, PROCESSED_SINGLE;
	}

	@Element
	private Party from;
	
	@Element
	private Party to;
	
	@Element
	private String description;
	
	@Attribute
	private String id;
	
	@Attribute
	private TransactionStatus status;
	
	@Attribute
	private String timestamp;

	public Party getFrom() {
		return from;
	}

	public void setFrom(Party from) {
		this.from = from;
	}

	public Party getTo() {
		return to;
	}

	public void setTo(Party to) {
		this.to = to;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
