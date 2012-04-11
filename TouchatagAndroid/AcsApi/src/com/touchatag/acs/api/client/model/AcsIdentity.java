package com.touchatag.acs.api.client.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name="acsIdentity")
@Namespace(prefix="ns2", reference="http://acs.touchatag.com/schema/acsIdentity-1.0")
public class AcsIdentity {

	@Element
	private String identityId;
	
	@Element
	private boolean tagOwnerScopeAllowed;
	
	@ElementList(entry="role")
	private List<String> roles = new ArrayList<String>();
	
	@ElementList(entry="trustedTagOwner")
	private List<String> trustedTagOwners = new ArrayList<String>();
	
	@ElementList(entry="acquiryDelegate")
	private List<String> acquiryDelegates = new ArrayList<String>();
	
	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public boolean isTagOwnerScopeAllowed() {
		return tagOwnerScopeAllowed;
	}

	public void setTagOwnerScopeAllowed(boolean tagOwnerScopeAllowed) {
		this.tagOwnerScopeAllowed = tagOwnerScopeAllowed;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getTrustedTagOwners() {
		return trustedTagOwners;
	}

	public void setTrustedTagOwners(List<String> trustedTagOwners) {
		this.trustedTagOwners = trustedTagOwners;
	}

	public List<String> getAcquiryDelegates() {
		return acquiryDelegates;
	}

	public void setAcquiryDelegates(List<String> acquiryDelegates) {
		this.acquiryDelegates = acquiryDelegates;
	}
	
}
