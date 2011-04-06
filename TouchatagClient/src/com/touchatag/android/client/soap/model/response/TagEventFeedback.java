package com.touchatag.android.client.soap.model.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.touchatag.android.client.soap.command.ResponseDTO;

public class TagEventFeedback implements ResponseDTO, Serializable{

	private String systemMessage;
	
	private TransactionFeedback transactionFeedback;
	
	private Map<String, ApplicationResponse> applicationResponses = new HashMap<String, ApplicationResponse>();

	public String getSystemMessage() {
		return systemMessage;
	}

	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	public TransactionFeedback getTransactionFeedback() {
		return transactionFeedback;
	}

	public void setTransactionFeedback(TransactionFeedback transactionFeedback) {
		this.transactionFeedback = transactionFeedback;
	}

	public Map<String, ApplicationResponse> getApplicationResponses() {
		return applicationResponses;
	}

	
	
	
}
