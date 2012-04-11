package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.touchatag.android.correlation.api.v1_2.command.RequestDTO;

@Root(name="Body")
public class SoapBody {

	@Element(name="ns2:ping", required = false)
	private PingEvent pingEvent;

	@Element(name="ns2:handleTagEvent", required = false)
	private TagEvent tagEvent;
	
	@Element(name="handleTagEventResponse", required = false)
	private TagEventResponse tagEventResponse;
	
	@Element(name="Fault", required=false)
	private Fault fault;

	public SoapBody(){};
	
	public SoapBody(RequestDTO request) {
		super();
		if(request instanceof PingEvent){
			this.pingEvent = (PingEvent)request;
		} else if(request instanceof TagEvent){
			this.tagEvent = (TagEvent)request;
		}
	}
	
	public SoapBody(PingEvent pingEvent) {
		super();
		this.pingEvent = pingEvent;
	}

	public SoapBody(TagEvent tagEvent) {
		super();
		this.tagEvent = tagEvent;
	}
	
	public SoapBody(Fault fault) {
		super();
		this.fault = fault;
	}

	public PingEvent getPingEvent() {
		return pingEvent;
	}

	public TagEvent getTagEvent() {
		return tagEvent;
	}

	public Fault getFault() {
		return fault;
	}

	public TagEventResponse getTagEventResponse() {
		return tagEventResponse;
	}

}
