package com.touchatag.acs.api.client.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference="http://acs.touchatag.com/schema/tag-1.0")
public class Tag {

	@Attribute
	private TagType type;
	
	@Attribute(required=true, name="ownerid")
	private String ownerId;
	
	@Attribute(required=true)
	private String identifier;
	
	@Attribute(required=true)
	private String hash;
	
	@Attribute(required=false)
	private boolean disabled;
	
	@Attribute(required=false)
	private String created;
	
	@Attribute(required=false, name="claimingrule")
	private ClaimingRule claimingRule;
	
	private String tagIdHash;
	
	@Attribute(required=false)
	private String shortcode;
	
	private boolean linked;
	
	private Application linkedApp;

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getCreated() {
		return created;
	}
	
	
	public void setCreated(String created) {
		this.created = created;
	}

	public ClaimingRule getClaimingRule() {
		return claimingRule;
	}

	public void setClaimingRule(ClaimingRule claimingRule) {
		this.claimingRule = claimingRule;
	}

	public String getTagIdHash() {
		return tagIdHash;
	}

	public void setTagIdHash(String tagIdHash) {
		this.tagIdHash = tagIdHash;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
	
	public Application getLinkedApp() {
		return linkedApp;
	}

	public void setLinkedApp(Application linkedApp) {
		this.linkedApp = linkedApp;
	}
	
	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

}
