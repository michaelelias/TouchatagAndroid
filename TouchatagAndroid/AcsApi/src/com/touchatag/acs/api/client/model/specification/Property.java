package com.touchatag.acs.api.client.model.specification;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class Property implements Serializable {

	@Attribute(required=true)
	private String name;
	
	@Element(required=false)
	private String text;
	
	@Element(required=false)
	private String uri;
	
	@Attribute(required=false)
	private String as;

	public Property(){};
	
	public Property(PropertyType type){
		this.name = type.getType();
	}
	
	public PropertyType getType() {
		if(text != null){
			return PropertyType.TEXT;
		}
		if(uri != null){
			return PropertyType.URI;
		}
		if(text != null){
			return PropertyType.DELEGATE;
		}
		return null;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAs() {
		return as;
	}

	public void setAs(String as) {
		this.as = as;
	}
	
}
