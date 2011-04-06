package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;

public class Command  implements Serializable {
	
	public String name;
	public String id;
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<command");
		if(name != null){
			sb.append(" name=\"" + name + "\"");
		}
		if(id != null){
			sb.append(" id=\"" + id + "\"");
		}
		sb.append("/>");
		return sb.toString();
	}
	
}
