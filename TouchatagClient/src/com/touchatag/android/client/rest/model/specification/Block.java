package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

	protected String nodeName = "block";
	public List<Property> properties = new ArrayList<Property>();
	public String id;
	public String ref;
	
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<" + nodeName);
		if(ref != null){
			sb.append(" ref=\"" + ref + "\"");
		}
		if(id != null){
			sb.append(" id=\"" + id + "\"");
		}
		if(properties.size() > 0){
			sb.append(">");
			for(Property prop : properties){
				sb.append(prop.toXml());
			}
			sb.append("</" + nodeName + ">");
		} else {
			sb.append("/>");
		}
		return sb.toString();
	}
}
