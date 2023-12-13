package com.heidigi.model;

import lombok.Data;

@Data
public class LoginStatusDTO {
	
	String userId="0";
	String userType="NONE";
	Boolean loginStatus=false;
	String jwt="";
	String loginId="0";	
	
	String message = "";
	
	
	
	

}
