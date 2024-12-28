package com.heidigi.jwt;

import java.io.Serializable;

public class JwtRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9007900444264498076L;
	private String username;
	private String password;

	// need default constructor for JSON Parsing
	public JwtRequest() {

	}

	public JwtRequest(String username, String password) {
		// System.out.println("entered in JwtRequest...");
		// System.out.println(username+","+password);
		this.setUsername(username);
		this.setPassword(password);
		// System.out.println("exited in JwtRequest...");
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
