package com.touchatag.beta.client.soap.model.common;

import java.util.ArrayList;
import java.util.List;


public class UserDTO {

	private String username;
	private byte[] password;
	private String passwordCleartext;
	private String displayName;
	private String email;
	private List<Role> roles = new ArrayList<Role>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getPasswordCleartext() {
		return passwordCleartext;
	}

	public void setPasswordCleartext(String passwordCleartext) {
		this.passwordCleartext = passwordCleartext;
	}

}
