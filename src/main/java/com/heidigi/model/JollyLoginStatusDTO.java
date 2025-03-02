package com.heidigi.model;

import lombok.Data;

@Data
public class JollyLoginStatusDTO {

	String userId = "0";
	String userType = "NONE";
	Boolean loginStatus = false;
	String jwt = "";
	String loginId = "0";

	String message = "";
	
	String name,email,mobile;
	

}
