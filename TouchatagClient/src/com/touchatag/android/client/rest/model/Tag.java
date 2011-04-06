package com.touchatag.android.client.rest.model;


public class Tag {

	private String type;
	
	private String ownerId;
	
	private String identifier;
	
	private String hash;
	
	private boolean disabled;
	
	private String created;
	
	private ClaimingRule claimingRule;
	
	private String tagIdHash;
	
	private String shortcode;
	
	private boolean linked;
	
	private Application linkedApp;

	public String getType() {
		return type;
	}

	public void setType(String type) {
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
