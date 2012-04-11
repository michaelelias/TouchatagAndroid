package com.touchatag.beta.client.soap.model.request;

import java.io.Serializable;

import com.touchatag.beta.client.soap.command.RequestDTO;
import com.touchatag.beta.client.soap.model.common.ClientId;
import com.touchatag.beta.client.soap.model.common.ReaderId;
import com.touchatag.beta.client.soap.model.common.TagEventType;
import com.touchatag.beta.client.soap.model.common.TagInfo;


public class TagEvent implements RequestDTO, Serializable{

	private TagEventType tagEventType;
	private ClientId clientId;
	private ReaderId readerId;
	private TagInfo actionTag;
	private TagInfo contextTag;

	public TagEventType getTagEventType() {
		return tagEventType;
	}

	public void setTagEventType(TagEventType tagEventType) {
		this.tagEventType = tagEventType;
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}

	public ReaderId getReaderId() {
		return readerId;
	}

	public void setReaderId(ReaderId readerId) {
		this.readerId = readerId;
	}

	public TagInfo getActionTag() {
		return actionTag;
	}

	public void setActionTag(TagInfo actionTag) {
		this.actionTag = actionTag;
	}

	public TagInfo getContextTag() {
		return contextTag;
	}

	public void setContextTag(TagInfo contextTag) {
		this.contextTag = contextTag;
	}

	// private List<TagAttachment> attachments;
	// private TransactionInfo transactionInfo;

}
