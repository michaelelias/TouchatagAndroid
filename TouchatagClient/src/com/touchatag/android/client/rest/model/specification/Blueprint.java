package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;

public class Blueprint implements Serializable {

	public SuperBlock superBlock;
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<blueprint>");
		if(superBlock != null){
			sb.append(superBlock.toXml());
		}
		sb.append("</blueprint>");
		return sb.toString();
	}
}
