package com.touchatag.android.client.rest.model.specification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Breakdown implements Serializable {

	public List<Block> blocks = new ArrayList<Block>();
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<breakdown");
		
		sb.append("</breakdown>");
		return sb.toString();
	}
}
