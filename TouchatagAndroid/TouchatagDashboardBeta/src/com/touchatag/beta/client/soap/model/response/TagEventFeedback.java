package com.touchatag.beta.client.soap.model.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.touchatag.beta.client.soap.command.ResponseDTO;

public class TagEventFeedback implements ResponseDTO, Serializable{

	public static final String TAG_DISABLED = "Tag disabled";
	public static final String TAG_RECONFIGURATION = "Tag (re)configuration in progress...";
	
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
