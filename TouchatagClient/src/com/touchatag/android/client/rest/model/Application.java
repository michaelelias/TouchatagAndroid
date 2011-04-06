package com.touchatag.android.client.rest.model;

import java.util.Date;

import com.touchatag.android.client.rest.model.specification.Specification;

public class Application {

	private String id;
	private String ownerId;
	private String created;
	private Specification specification;
	private String name;
	private String description;
	private String template;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getCommand(){
		if(specification == null){
			throw new RuntimeException("Cannot created command without a specification");
		}
		return (getId() + "::" + specification.commands.get(0).id);
	}
}
