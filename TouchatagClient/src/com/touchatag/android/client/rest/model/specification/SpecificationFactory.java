package com.touchatag.android.client.rest.model.specification;

import com.touchatag.android.client.rest.model.specification.Property.PropertyType;

public class SpecificationFactory {

	public static Specification createSimpleWebLinkSpec(String uri){
		Specification spec = new Specification();
		Command command = new Command();
		command.name = "default";
		command.id = "default";
		spec.commands.add(command);
		
		SuperBlock superblock = new SuperBlock();
		superblock.id = "*";
		superblock.ref = "urn:touchatag:block:web-link-app";
		
		Property prop = new Property();
		prop.type = PropertyType.URI;
		prop.value = uri;
		superblock.properties.add(prop);
		
		Blueprint blueprint = new Blueprint();
		blueprint.superBlock = superblock;
		spec.blueprint = blueprint;
		return spec;
	}
	
}
