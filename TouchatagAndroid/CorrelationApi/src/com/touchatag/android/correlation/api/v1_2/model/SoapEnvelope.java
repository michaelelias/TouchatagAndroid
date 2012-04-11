package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name="Envelope", strict=false)
@Namespace(prefix="S", reference="http://schemas.xmlsoap.org/soap/envelope/")
public class SoapEnvelope {

	@Element(name="Body")
	public SoapBody body;

	public SoapEnvelope(){};
	
	public SoapEnvelope(SoapBody body) {
		super();
		this.body = body;
	}
	
}
