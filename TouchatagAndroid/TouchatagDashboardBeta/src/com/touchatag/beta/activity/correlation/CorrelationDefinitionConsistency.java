package com.touchatag.beta.activity.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.touchatag.acs.api.client.model.ruleset.Association;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.store.ApplicationStore;

public class CorrelationDefinitionConsistency {

	/**
	 * Checks the given CorrelationDefinition for non existing or faulty commands and cleans them up.
	 * 
	 * @param corrDef
	 * @param appStore
	 * @return true if the given CorrelationDefinition has been modified
	 */
	public static boolean cleanup(CorrelationDefinition corrDef, ApplicationStore appStore){
		boolean modified = false;
		List<Association> associations = new ArrayList<Association>(corrDef.getAssociations());
		List<String> validAppIds = new ArrayList<String>();
		for(Association asso : associations){
			String command = asso.getCommand();
			if(!command.contains("::")){
				corrDef.getAssociations().remove(asso);
				modified = true;
			}
			validAppIds.add(command.split("::")[0]);
		}
		if(validAppIds.size() > 0){
			Map<String, Boolean> existsMap = appStore.exists(validAppIds);
			for(Entry<String, Boolean> entry : existsMap.entrySet()){
				if(!entry.getValue()){
					corrDef.removeAssociationsByApplicationId(entry.getKey());
					modified = true;
				}
			}
		}
		return modified;
	}
	
}
