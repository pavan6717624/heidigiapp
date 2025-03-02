package com.heidigi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heidigi.model.HeidigiLoginDTO;
import com.heidigi.model.LoginStatusDTO;
import com.heidigi.service.ScanShopService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "SCANSHOP")
public class ScanShopController {
	
	@Autowired
	ScanShopService service;

	@RequestMapping(value = "/demo")
	public String demo() throws Exception {

		return "demo";

	}
	
	@RequestMapping(value = "/getLoginDetails")
	public LoginStatusDTO getLoginDetails() throws Exception {

		LoginStatusDTO loginStatus = new LoginStatusDTO();

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			loginStatus.setUserId("");

			loginStatus.setLoginStatus(false);

			loginStatus.setUserType("");
		} else {

			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();

			loginStatus.setUserId(userDetails.getUsername());

			loginStatus.setLoginStatus(true);

			System.out.println(Long.valueOf(userDetails.getUsername()));

			loginStatus.setUserType(userDetails.getAuthorities().toArray()[0].toString());
		}
		

		return loginStatus;
	}
	
	
	@RequestMapping(value = "sslogin")
	public LoginStatusDTO login(@RequestBody HeidigiLoginDTO login) {

		return service.login(login);

	}

}
