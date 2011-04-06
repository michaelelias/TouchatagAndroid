package com.touchatag.android.client.soap.model.request;

import java.io.Serializable;

import com.touchatag.android.client.soap.command.RequestDTO;
import com.touchatag.android.client.soap.model.common.ClientId;

public class PingEvent implements RequestDTO, Serializable {

	private ClientId clientId;

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}



}
