package com.heidigi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heidigi.model.DropDown;
import com.heidigi.model.JollyLocationDTO;
import com.heidigi.model.JollyLoginDTO;
import com.heidigi.model.JollyLoginStatusDTO;
import com.heidigi.model.JollySignupDTO;
import com.heidigi.model.JollyTripDTO;
import com.heidigi.service.JollyServiceClass;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "JOLLY")
public class JollyController {

	@Autowired
	JollyServiceClass service;

	@RequestMapping(value = "login")
	public JollyLoginStatusDTO login(@RequestBody JollyLoginDTO login) {

		return service.login(login);

	}

	@RequestMapping(value = "sendOTP")
	public Boolean sendOTP(String mobile) throws Exception {

		return service.sendOTP(mobile);

	}

	@RequestMapping(value = "verifyOTP")
	public JollyLoginStatusDTO verifyOTP(String mail, String mobile, String password) throws Exception {

		return service.verifyOTP(mail, password, mobile);

	}

	@RequestMapping(value = "signup")
	public JollyLoginStatusDTO signup(@RequestBody JollySignupDTO signup) throws Exception {
		return service.signup(signup);
	}

	@RequestMapping(value = "/getLoginDetails")
	public JollyLoginStatusDTO getLoginDetails() throws Exception {
		return service.getLoginDetails();

	}

	@RequestMapping(value = "/addLocation")
	public JollyLocationDTO addLocation(@RequestBody JollyLocationDTO locationDTO) throws Exception {
		return service.addLocation(locationDTO);

	}
	
	@RequestMapping(value = "/addTrip")
	public JollyTripDTO addTrip(@RequestBody JollyTripDTO tripDTO) throws Exception {
		return service.addTrip(tripDTO);

	}
	
	@RequestMapping(value = "/editLocation")
	public JollyLocationDTO editLocation(@RequestBody JollyLocationDTO locationDTO) throws Exception {
		return service.editLocation(locationDTO);

	}

	@RequestMapping(value = "/getLocations")
	public List<JollyLocationDTO> getLocations() throws Exception {
		return service.getLocations();

	}
	
	@RequestMapping(value = "/getTrips")
	public List<JollyTripDTO> getTrips() throws Exception {
		return service.getTrips();

	}
	
	@RequestMapping(value = "/getLocationDropDown")
	public List<DropDown> getLocationDropDown() throws Exception {
		return service.getLocationDropDown();

	}
	
	@RequestMapping(value = "/deleteLocation")
	public Boolean deleteLocation(String locationName) throws Exception {
		return service.deleteLocation(locationName);

	}

}
