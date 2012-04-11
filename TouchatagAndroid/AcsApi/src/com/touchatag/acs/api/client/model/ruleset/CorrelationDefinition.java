package com.touchatag.acs.api.client.model.ruleset;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;


@Root(name="ns1:correlationDefinition")
@NamespaceList({
	@Namespace(prefix="ns3", reference="http://acs.touchatag.com/schema/ruleset-1.0"),
	@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/associations-1.0"),
	@Namespace(prefix="ns1", reference="http://acs.touchatag.com/schema/correlationDefinition-1.0")
})
public class CorrelationDefinition {

	@Attribute(required=false, name="ownerid")
	private String ownerId;
	
	@Element(required=false)
	private Associations associations = new Associations();
	
	@Element(required=false, name="ruleset")
	private List<Rule> rules;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<Association> getAssociations() {
		return associations.getAssociations();
	}

	public void setAssociations(List<Association> associations) {
		this.associations.setAssociations(associations);
	}

	public List<Association> getAssociationsForCommand(String commandId){
		List<Association> commandAssociations = new ArrayList<Association>();
		for(Association asso : getAssociations()){
			if(asso.getCommand().equals(commandId)){
				commandAssociations.add(asso);
			}
		}
		return commandAssociations;
	}
	
	public List<Association> getAssociationsForTag(String tagHash){
		List<Association> tagAssociations = new ArrayList<Association>();
		for(Association asso : getAssociations()){
			if(tagHash.equals(asso.getTagId())){
				tagAssociations.add(asso);
			}
		}
		return tagAssociations;
	}
	
	public void associateTagToCommand(String tagHash, String command){
		List<Association> tagAssociations = getAssociationsForTag(tagHash);
		for(Association asso : tagAssociations){
			getAssociations().remove(asso);
		}
		Association asso = new Association();
		asso.setTagId(tagHash);
		asso.setCommand(command);
		getAssociations().add(asso);
	}
	
	public void removeAssociationsByApplicationId(String appId){
		List<Association> appAssociations = new ArrayList<Association>();
		for(Association asso : getAssociations()){
			if(appId.equals(asso.getCommand().split("::")[0])){
				appAssociations.add(asso);
			}
		}
		for(Association asso : appAssociations){
			getAssociations().remove(asso);
		}
	}
	
	public boolean removeAssociationsByTagHash(String tagHash){
		List<Association> tagAssociations = new ArrayList<Association>();
		for(Association asso : getAssociations()){
			if(tagHash.equals(asso.getTagId())){
				tagAssociations.add(asso);
			}
		}
		for(Association asso : tagAssociations){
			getAssociations().remove(asso);
		}
		return tagAssociations.size() > 0;
	}
	
	public String getAssociatedAppIdForTag(String tagHash){
		List<Association> associations = getAssociationsForTag(tagHash);
		for(Association asso : associations){
			String command = asso.getCommand();
			return command.split("::")[0];
		}
		return null;
	}
	
}
