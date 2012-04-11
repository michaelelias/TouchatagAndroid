package com.touchatag.acs.api.client.model.ruleset;

import org.simpleframework.xml.Element;

@Element
public class Rule {

	@Element
	private Expression expression;
	
	@Element
	private Rule rule;
	
	@Element
	private Launch launch;
	
}
