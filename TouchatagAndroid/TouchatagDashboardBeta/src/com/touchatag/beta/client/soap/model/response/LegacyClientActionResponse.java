package com.touchatag.beta.client.soap.model.response;

import java.io.Serializable;

public class LegacyClientActionResponse extends ApplicationResponse implements Serializable{

	private ClientAction clientAction;

	public ClientAction getClientAction() {
		return clientAction;
	}

	public void setClientAction(ClientAction clientAction) {
		this.clientAction = clientAction;
	}

}
