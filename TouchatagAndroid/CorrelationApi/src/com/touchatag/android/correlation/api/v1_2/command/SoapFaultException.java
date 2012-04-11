package com.touchatag.android.correlation.api.v1_2.command;

import com.touchatag.android.correlation.api.v1_2.model.Fault;

public class SoapFaultException extends Exception {

	public SoapFaultException(Fault fault){
		super("SoapFault[code=" + fault.code + ", msg=" + fault.message + "]");
	}
	
}
