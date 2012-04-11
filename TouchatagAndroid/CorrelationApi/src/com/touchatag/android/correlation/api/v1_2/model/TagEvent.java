package com.touchatag.android.correlation.api.v1_2.model;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import com.touchatag.android.correlation.api.v1_2.command.RequestDTO;

@Root
@NamespaceList({
	@Namespace(prefix="ns3", reference="http://www.touchatag.com/acs/api/correlation-1.1"),
	@Namespace(prefix="ns2", reference="http://www.touchatag.com/acs/api/correlation-1.2"),
})
public class TagEvent implements RequestDTO, Serializable{

	@Attribute(required=true)
	private TagEventType tagEventType;

	@Element(required=true)
	private ClientId clientId;

	@Element(required=true)
	private ReaderId readerId;

	@Element(required=true)
	private TagInfo actionTag;

	@Element(required=false)
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

}
