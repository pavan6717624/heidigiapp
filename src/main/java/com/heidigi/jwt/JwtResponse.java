package com.heidigi.jwt;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = 5166255348328331015L;
	private final String jwttoken;

	public JwtResponse(String jwttoken) {
		//System.out.println("entered in JwtResponse constructor...");
		this.jwttoken = jwttoken;
		//System.out.println("exited in JwtResponse constructor...");
	}

	public String getToken() {
		//System.out.println("entered in JwtResponse getToken...");
		return this.jwttoken;
	}
}