package com.touchatag.android.client.rest.model;

import java.util.ArrayList;
import java.util.List;

public class CorrelationDefinition {

	private String ownerId;
	private List<Association> associations = new ArrayList<Association>();

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<Association> getAssociations() {
		return associations;
	}

	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}

	public List<Association> getAssociationsForCommand(String commandId){
		List<Association> commandAssociations = new ArrayList<Association>();
		for(Association asso : associations){
			if(asso.getCommand().equals(commandId)){
				commandAssociations.add(asso);
			}
		}
		return commandAssociations;
	}
	
	public List<Association> getAssociationsForTag(String tagHash){
		List<Association> tagAssociations = new ArrayList<Association>();
		for(Association asso : associations){
			if(asso.getTagId().equals(tagHash)){
				tagAssociations.add(asso);
			}
		}
		return tagAssociations;
	}
	
	public void associateTagToCommand(String tagHash, String command){
		List<Association> tagAssociations = getAssociationsForTag(tagHash);
		for(Association asso : tagAssociations){
			associations.remove(asso);
		}
		Association asso = new Association();
		asso.setTagId(tagHash);
		asso.setCommand(command);
		associations.add(asso);
	}
	
	public void removeAssociationsByApplicationId(String appId){
		List<Association> appAssociations = new ArrayList<Association>();
		for(Association asso : associations){
			if(appId.equals(asso.getCommand().split("::")[0])){
				appAssociations.add(asso);
			}
		}
		for(Association asso : appAssociations){
			associations.remove(asso);
		}
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
