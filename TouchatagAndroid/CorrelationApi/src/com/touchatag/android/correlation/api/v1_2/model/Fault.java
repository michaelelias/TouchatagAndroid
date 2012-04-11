package com.touchatag.android.correlation.api.v1_2.model;

import org.simpleframework.xml.Element;

@Element
public class Fault {

	@Element(name="faultcode")
	public String code;
	
	@Element(name="faultstring")
	public String message;
}
//<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body><env:Fault xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><faultcode>env:Client</faultcode><faultstring>Endpoint {http://www.touchatag.com/acs/api/correlation-1.2}CorrelationProxyBeanV1_2Port does not contain operation meta data for: {http://www.touchatag.com/acs/api/correlation-1.3}tagEvent</faultstring></env:Fault></env:Body></env:Envelope>