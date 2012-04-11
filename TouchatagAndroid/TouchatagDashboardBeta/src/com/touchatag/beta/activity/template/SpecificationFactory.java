package com.touchatag.beta.activity.template;

import java.util.Map;

import com.touchatag.acs.api.client.model.specification.Block;
import com.touchatag.acs.api.client.model.specification.Blueprint;
import com.touchatag.acs.api.client.model.specification.Breakdown;
import com.touchatag.acs.api.client.model.specification.Command;
import com.touchatag.acs.api.client.model.specification.Delegate;
import com.touchatag.acs.api.client.model.specification.Property;
import com.touchatag.acs.api.client.model.specification.PropertyType;
import com.touchatag.acs.api.client.model.specification.Shunt;
import com.touchatag.acs.api.client.model.specification.Specification;
import com.touchatag.acs.api.client.model.specification.SuperBlock;
import com.touchatag.acs.api.client.model.specification.Wire;
import com.touchatag.acs.api.client.model.specification.WiringScheme;

public class SpecificationFactory {

	public static Specification createSimpleWebLinkSpec(String uri){
		Specification spec = new Specification();
		Command command = new Command();
		command.name = "default";
		command.id = "default";
		spec.application.commands.add(command);
		
		SuperBlock superblock = new SuperBlock();
		superblock.id = "*";
		superblock.ref = "urn:touchatag:block:web-link-app";
		
		Property prop = new Property(PropertyType.URI);
		prop.setUri(uri);
		prop.setName("URI");
		superblock.properties.add(prop);
		
		Blueprint blueprint = new Blueprint();
		blueprint.superBlock = superblock;
		spec.blueprint = blueprint;
		return spec;
	}
	
	public static Specification createWebLinkSpecWithJavascript(String uri, String javascript, Map<String, String> params){
		Specification spec = new Specification();
		Command command = new Command();
		command.name = "default";
		command.id = "default";
		spec.application.commands.add(command);
		
		SuperBlock superblock = new SuperBlock();
		superblock.id = "*";
		
		Property prop = new Property(PropertyType.URI);
		prop.setUri(uri);
		prop.setName("link");
		superblock.properties.add(prop);
		
		prop = new Property(PropertyType.TEXT);
		prop.setText("<![CDATA[" + javascript + " ]]>");
		prop.setName("javascript");
		superblock.properties.add(prop);
		
		if(params != null){
			for(Map.Entry<String, String> param : params.entrySet()){
				prop = new Property(PropertyType.TEXT);
				prop.setText(param.getValue());
				prop.setName(param.getKey());
				superblock.properties.add(prop);
			}
		}
		
		Breakdown breakdown = new Breakdown();
		
		Block block = new Block();
		block.id = "uri";
		block.ref = "urn:touchatag:block:uri-source";
		Delegate delegate = new Delegate();
		delegate.as = "link";
		prop = new Property(PropertyType.DELEGATE);
		prop.setName("Value"); 
		prop.setAs(delegate.as);
		block.properties.add(prop);
		breakdown.blocks.add(block);
		
		block = new Block();
		block.id = "script";
		block.ref = "urn:touchatag:block:text-source";
		delegate = new Delegate();
		delegate.as = "javascript";
		prop = new Property(PropertyType.DELEGATE);
		prop.setName("Value"); 
		prop.setAs(delegate.as);
		block.properties.add(prop);
		breakdown.blocks.add(block);
		
		if(params != null){
			for(Map.Entry<String, String> param : params.entrySet()){
				block = new Block();
				block.id = param.getKey() + "-textsource";
				block.ref = "urn:touchatag:block:text-source";
				delegate = new Delegate();
				delegate.as = param.getKey();
				prop = new Property(PropertyType.DELEGATE);
				prop.setName("Value"); 
				prop.setAs(delegate.as);
				block.properties.add(prop);
				breakdown.blocks.add(block);
			}
		}
		
		block = new Block();
		block.id = "js-engine";
		block.ref = "urn:touchatag:block:script-engine";
		breakdown.blocks.add(block);
		
		block = new Block();
		block.id = "weblink";
		block.ref = "urn:touchatag:block:web-link";
		breakdown.blocks.add(block);
		
		block = new Block();
		block.id = "script-success-sink";
		block.ref = "urn:touchatag:block:boolean-sink";
		breakdown.blocks.add(block);
		
		if(params != null){
			for(Map.Entry<String, String> param : params.entrySet()){
				block = new Block();
				block.id = param.getKey() + "-textsink";
				block.ref = "urn:touchatag:block:text-sink";
				breakdown.blocks.add(block);
			}
		}
		
		WiringScheme wiringScheme = new WiringScheme();
		
		if(params != null){
			for(Map.Entry<String, String> param : params.entrySet()){
				Wire wire = new Wire();
				wire.from = param.getKey() + "-textsource:out";
				wire.to = param.getKey() + "-textsink:in";
				wiringScheme.wires.add(wire);
			}
		}
		
		Wire wire = new Wire();
		wire.from = "script:out";
		wire.to = "js-engine:script-in";
		wiringScheme.wires.add(wire);
		
		wire = new Wire();
		wire.from = "js-engine:success-out";
		wire.to = "script-success-sink:in";
		wiringScheme.wires.add(wire);
		
		wire = new Wire();
		wire.from = "uri:out";
		wire.to = "weblink:in";
		wiringScheme.wires.add(wire);
		
		Shunt shunt = new Shunt();
		shunt.from = "weblink:out";
		shunt.to = "action-out";
		wiringScheme.wires.add(shunt);
		
		superblock.breakdown = breakdown;
		superblock.wiringscheme = wiringScheme;
		
		Blueprint blueprint = new Blueprint();
		blueprint.superBlock = superblock;
		spec.blueprint = blueprint;
		
		return spec;
	}
	
	public static Specification addTextPropertyToSuperblock(Specification spec, String name, String value){
		Property prop = new Property(PropertyType.TEXT);
		prop.setName(name);
		prop.setText(value);
		spec.blueprint.superBlock.properties.add(prop);
		return spec;
	}
	
}
