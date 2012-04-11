package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import com.touchatag.acs.api.client.model.specification.Specification;

@Root(name="ns2:application")
@NamespaceList({
	@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/application-1.0"),
	@Namespace(reference="http://acs.touchatag.com/schema/specification-1.1")
})
public class Application {

	@Attribute(required=false)
	private String id;
	
	@Attribute(required=false, name="ownerid")
	private String ownerId;
	
	@Attribute(required=false)
	private String created;
	
	@Element(required=false)
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
			//throw new RuntimeException("Cannot created command without a specification");
			// use default command
			return getId() + "::default";
		}
		return (getId() + "::" + specification.application.commands.get(0).id);
	}
}
