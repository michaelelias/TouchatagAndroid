package com.touchatag.android.correlation.api.v1_2.model;

import java.io.Serializable;

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
public class PingEvent implements RequestDTO, Serializable {

	@Element
	private ClientId clientId;

	public PingEvent(ClientId clientId) {
		super();
		this.clientId = clientId;
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}



}
