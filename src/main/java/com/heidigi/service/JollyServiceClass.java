package com.heidigi.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.heidigi.domain.JollyLocation;
import com.heidigi.domain.JollySchedule;
import com.heidigi.domain.JollyTrip;
import com.heidigi.domain.JollyUser;
import com.heidigi.jwt.JwtTokenUtil;
import com.heidigi.model.DropDown;
import com.heidigi.model.JollyCalendarDTO;
import com.heidigi.model.JollyCustomerDTO;
import com.heidigi.model.JollyLocationDTO;
import com.heidigi.model.JollyLoginDTO;
import com.heidigi.model.JollyLoginStatusDTO;
import com.heidigi.model.JollyScheduleDTO;
import com.heidigi.model.JollySignupDTO;
import com.heidigi.model.JollyTripDTO;
import com.heidigi.repository.JollyCustomerRepository;
import com.heidigi.repository.JollyLocationRepository;
import com.heidigi.repository.JollyRoleRepository;
import com.heidigi.repository.JollyScheduleRepository;
import com.heidigi.repository.JollyTripRepository;
import com.heidigi.repository.JollyUserRepository;

@Service
public class JollyServiceClass {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	JollyUserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Autowired
	JollyRoleRepository roleRepository;

	@Autowired
	JollyLocationRepository locationRepository;

	@Autowired
	JollyTripRepository tripRepository;

	@Autowired
	JollyCustomerRepository customerRepository;

	@Autowired
	JollyScheduleRepository scheduleRepository;

	@Value("${email.password}")
	private String password;

	public Boolean sendOTP(String mobile) throws Exception {
		try {

			JollyUser user = userRepository.findByMobile(mobile).get();

			String password = generateOTP(4);

			user.setPassword(password);
			userRepository.save(user);
			return sendMessage(user.getEmail(), "Jolly Vacations - OTP", "Your OTP is " + password);
		} catch (Exception ex) {
			return false;
		}

	}

	public JollyTripDTO addTrip(JollyTripDTO tripDTO) throws Exception {

		List<JollyTrip> trip = tripRepository.findByTrip(tripDTO.getFromDate(), tripDTO.getToDate());
		JollyTripDTO status = new JollyTripDTO();
		if (trip.size() != 0) {

			status.setStatus(false);
			status.setMessage("Trip already exists..");

		} else {
			JollyTrip jtrip = new JollyTrip();
			jtrip.setLocation(locationRepository
					.findByLocationNameIgnoreCaseOrderByLocationIdDesc(tripDTO.getLocationName()).get());
			jtrip.setFromDate(tripDTO.getFromDate());
			jtrip.setToDate(tripDTO.getToDate());
			tripRepository.save(jtrip);

			status.setStatus(true);
			status.setMessage("Trip Added Successfully..");
		}

		return status;
	}

	public JollyCustomerDTO addCustomer(JollyCustomerDTO customerDTO) throws Exception {

		Optional<JollyUser> customers = userRepository.findByCustomerMobile(customerDTO.getMobile());
		JollyCustomerDTO status = new JollyCustomerDTO();
		if (customers.isPresent()) {

			status.setStatus(false);
			status.setMessage("Customer already exists..");

		} else {
//			JollyCustomer customer = new JollyCustomer(customerDTO);
//
//			customerRepository.save(customer);

			JollyUser user = new JollyUser(customerDTO);
			user.setRole(roleRepository.findByRoleName("Customer").get());
			userRepository.save(user);

			status.setStatus(true);
			status.setMessage("Customer Added Successfully..");
		}

		return status;
	}

	public List<JollyCalendarDTO> getSchedules() {
		return scheduleRepository.getSchedules();
	}

	public JollyScheduleDTO addSchedule(JollyScheduleDTO scheduleDTO) throws Exception {

		System.out.println(scheduleDTO.getTripDates());

		final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		final LocalDate fromDate = LocalDate.parse(scheduleDTO.getTripDates().split("to")[0].trim(), dtf);
		final LocalDate toDate = LocalDate.parse(scheduleDTO.getTripDates().split("to")[1].trim(), dtf);

		JollyTrip trip = tripRepository.findByTrip(fromDate, toDate).stream()
				.filter(o -> o.getLocation().getLocationName().equals(scheduleDTO.getLocationName()))
				.collect(Collectors.toList()).get(0);

		JollyScheduleDTO status = new JollyScheduleDTO();

		JollyUser user = userRepository.findByMobile(scheduleDTO.getMobile()).get();

		Optional<JollySchedule> schedule = scheduleRepository.findByTripAndUser(trip, user);

		if (schedule.isPresent()) {
			status.setStatus(false);
			status.setMessage("Customer is not available for the Trip");
		} else {
			JollySchedule newSchedule = new JollySchedule();
			newSchedule.setUser(user);
			newSchedule.setTrip(trip);
			scheduleRepository.save(newSchedule);
			status.setStatus(true);
			status.setMessage("Customer added to Trip Successfully");
		}

		return status;
	}

	public JollyCustomerDTO editCustomer(JollyCustomerDTO customerDTO) throws Exception {

		Optional<JollyUser> customers = userRepository.findByCustomerMobile(customerDTO.getOldMobile());

		JollyCustomerDTO status = new JollyCustomerDTO();
		if (customers.isPresent()) {

			JollyUser customer = customers.get();

			customer.setEmail(customerDTO.getEmailId());
			customer.setMobile(customerDTO.getMobile());
			customer.setName(customerDTO.getName());
			userRepository.save(customer);

			status.setStatus(true);
			status.setMessage("Customer Details Edited Successfully..");

		} else {

			status.setStatus(false);
			status.setMessage("Customer Details Edit Failed..");
		}

		return status;
	}

	public JollyCustomerDTO deleteCustomer(JollyCustomerDTO customerDTO) throws Exception {

		Optional<JollyUser> customers = userRepository.findByCustomerMobile(customerDTO.getMobile());
		JollyCustomerDTO status = new JollyCustomerDTO();
		if (customers.isPresent()) {

			JollyUser customer = customers.get();
			customer.setIsDeleted(true);

			userRepository.save(customer);
			status.setStatus(true);
			status.setMessage("Customer Deleted Successfully..");

		} else {

			status.setStatus(false);
			status.setMessage("Customer Delete Failed..");
		}

		return status;
	}

	public JollyLocationDTO addLocation(JollyLocationDTO locationDTO) throws Exception {

		Optional<JollyLocation> location = locationRepository
				.findByLocationNameIgnoreCaseOrderByLocationIdDesc(locationDTO.getLocationName());
		JollyLocationDTO status = new JollyLocationDTO();
		if (location.isPresent()) {

			status.setStatus(false);
			status.setMessage("Location already exists..");

		} else {

			JollyLocation jlocation = new JollyLocation();
			jlocation.setLocationName(locationDTO.getLocationName());
			jlocation.setPrice(locationDTO.getPrice());
			locationRepository.save(jlocation);

			status.setStatus(true);
			status.setMessage("Location (" + locationDTO.getLocationName() + " , " + locationDTO.getPrice()
					+ ") Added Successfully..");

		}

		return status;

	}

	public JollyLocationDTO editLocation(JollyLocationDTO locationDTO) throws Exception {

		Optional<JollyLocation> location = locationRepository
				.findByLocationNameIgnoreCaseOrderByLocationIdDesc(locationDTO.getLocationName());
		JollyLocationDTO status = new JollyLocationDTO();
		if (location.isPresent()) {

			JollyLocation jlocation = location.get();
			jlocation.setLocationName(locationDTO.getLocationName());
			jlocation.setPrice(locationDTO.getPrice());
			locationRepository.save(jlocation);

			status.setStatus(true);
			status.setMessage("Location (" + locationDTO.getLocationName() + " , " + locationDTO.getPrice()
					+ ") Edited Successfully..");

		} else {

			status.setStatus(false);
			status.setMessage("Location does not exists..");

		}

		return status;

	}

	public List<JollyLocationDTO> getLocations() throws Exception {

		return locationRepository.findAll().stream()
				.sorted(Comparator.comparingDouble(JollyLocation::getLocationId).reversed())
				.map(o -> new JollyLocationDTO(o)).collect(Collectors.toList());

	}

	public List<JollyCustomerDTO> getCustomers() throws Exception {

		return userRepository.findAll().stream()
				.filter(o -> o.getRole().getRoleName().equals("Customer") && !o.getIsDisabled() && !o.getIsDeleted())
				.sorted(Comparator.comparingDouble(JollyUser::getUserId).reversed()).map(o -> new JollyCustomerDTO(o))
				.collect(Collectors.toList());

	}

	public List<JollyTripDTO> getTrips() throws Exception {

		return tripRepository.findAll().stream().sorted(Comparator.comparingDouble(JollyTrip::getTripId).reversed())
				.map(o -> new JollyTripDTO(o)).collect(Collectors.toList());

	}

	public Boolean deleteLocation(String locationName) throws Exception {

		Boolean status = false;

		Optional<JollyLocation> location = locationRepository
				.findByLocationNameIgnoreCaseOrderByLocationIdDesc(locationName);

		if (location.isPresent()) {
			locationRepository.delete(location.get());
			status = true;
		}

		return status;

	}

	public List<DropDown> getLocationDropDown() throws Exception {
		return getLocations().stream().map(o -> new DropDown(o.getLocationName(), o.getLocationName()))
				.collect(Collectors.toList());
	}

	public List<DropDown> getCustomersDropDown() throws Exception {
		return getCustomers().stream()
				.map(o -> new DropDown(o.getName() + " - " + o.getMobile(), o.getName() + " - " + o.getMobile()))
				.collect(Collectors.toList());
	}

	public JollyLoginStatusDTO getLoginDetails() throws Exception {

		JollyLoginStatusDTO loginStatus = new JollyLoginStatusDTO();

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			loginStatus.setUserId("");

			loginStatus.setLoginStatus(false);

			loginStatus.setUserType("");
		} else {

			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();

			JollyUser user = userRepository.findByMobile(userDetails.getUsername()).get();

			loginStatus.setUserId(userDetails.getUsername());

			loginStatus.setName(user.getName());

			loginStatus.setEmail(user.getEmail());

			loginStatus.setMobile(user.getMobile());

			loginStatus.setLoginStatus(true);

			System.out.println(Long.valueOf(userDetails.getUsername()));

			loginStatus.setUserType(userDetails.getAuthorities().toArray()[0].toString());
		}

		return loginStatus;
	}

	public JollyLoginStatusDTO verifyOTP(String mail, String password, String mobile) throws Exception {

		try {

			JollyUser user = userRepository.findByEmailOrMobile(mail, mobile).get();
			Boolean status = user.getPassword().equals(password);

			if (user.getIsDisabled()) {
				user.setIsDisabled(!status);
				userRepository.save(user);
			}
			JollyLoginDTO loginDTO = new JollyLoginDTO();
			loginDTO.setMobile(user.getMobile() + "");
			loginDTO.setPassword(password);

			return login(loginDTO);

		} catch (Exception ex) {

			System.out.println(ex);
			JollyLoginStatusDTO loginStatus = new JollyLoginStatusDTO();
			loginStatus.setLoginStatus(false);
			loginStatus.setMessage("Invalid Credientails..");

			return loginStatus;
		}
	}

	public Boolean sendMessage(String mailTo, String subject, String text) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(mailTo);
		msg.setSubject(subject);
		msg.setText(text);
		try {
			JavaMailSenderImpl jMailSender = (JavaMailSenderImpl) javaMailSender;

			jMailSender.setUsername("heidigiotp@gmail.com");
			jMailSender.setPassword(password);
			javaMailSender.send(msg);
		} catch (Exception ex) {
			try {
				javaMailSender.send(msg);
			} catch (Exception ex1) {
				System.out.println("Error in Sending Mail for " + mailTo + "\n" + ex);
			}

		}

		return true;
	}

	public JollyLoginStatusDTO login(JollyLoginDTO login) {
		JollyLoginStatusDTO loginStatus = new JollyLoginStatusDTO();
		String username = login.getMobile();
		String password = login.getPassword();
		try {

			Optional<JollyUser> userOpt = userRepository.findByMobile(username);

			if (userOpt.isPresent()) {

				authenticate(username, password);

				final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				final String token = jwtTokenUtil.generateToken(userDetails);

				loginStatus.setUserId(userDetails.getUsername());

				loginStatus.setLoginStatus(true);

				loginStatus.setJwt(token);

				loginStatus.setUserType(userDetails.getAuthorities().toArray()[0].toString());
			} else {
				System.out.println("He is not user");

				loginStatus.setLoginStatus(false);
				loginStatus.setMessage("Invalid Credientails..");

				return loginStatus;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error Occured while logging in " + ex);
			loginStatus.setLoginStatus(false);
			loginStatus.setMessage("Invalid Credientails..");
		}

		return loginStatus;

	}

	private void authenticate(String username, String password) throws Exception {
		// System.out.println("entered in authenticate sub function...");
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

	}

	public String generateOTP(int length) throws NoSuchAlgorithmException {

		String numbers = "123456789123456789123456789123456789";
		SecureRandom random = new SecureRandom();
		char[] password = new char[length];

		for (int i = 0; i < length; i++) {
			password[i] = numbers.charAt(random.nextInt(numbers.length()));
		}
		return new String(password);
	}

	public JollyLoginStatusDTO signup(JollySignupDTO signup) throws Exception {

		JollyLoginStatusDTO loginStatus = new JollyLoginStatusDTO();

		Optional<JollyUser> userOpt = userRepository.findByMobile(signup.getMobile());

		Optional<JollyUser> userOpt1 = userRepository.findByEmail(signup.getEmail());

		System.out.println("in singup");

		if (!userOpt.isPresent() && !userOpt1.isPresent()) {
			System.out.println("in singup2");
			JollyUser user = new JollyUser();
			user.setEmail(signup.getEmail());
			user.setMobile(signup.getMobile());
			user.setName(signup.getName());
			String otp = generateOTP(4);
			user.setPassword(otp);
			user.setMessage("User Signup");
			user.setRole(roleRepository.findByRoleName("Customer").get());
			user.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
			user.setIsDeleted(false);
			user.setIsDisabled(true);
//			if (categoryRepository.findByCname(signup.getCategory()).isPresent())
//				user.setCategory(categoryRepository.findByCname(signup.getCategory()).get());
			userRepository.save(user);
			loginStatus.setLoginStatus(
					userRepository.findByMobileAndPassword(user.getMobile(), user.getPassword()).isPresent());
			loginStatus.setMessage("Login Successful");
			sendMessage(signup.getEmail(), "Jolly Vacations - OTP", "Your OTP is " + otp);

		} else {
			System.out.println("in singup3 " + userOpt.isPresent() + " " + userOpt1.isPresent());
			loginStatus.setLoginStatus(false);
			if (userOpt.isPresent())
				loginStatus.setMessage(" Mobile number already Exists...");

			if (userOpt1.isPresent())
				loginStatus.setMessage(loginStatus.getMessage() + " Email already Exists...");
		}

		return loginStatus;

	}

	public Boolean removeFromTrip(String locationName, String trip, String customer) {

		try {

			final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			final LocalDate fromDate = LocalDate.parse(trip.split("to")[0].trim(), dtf);
			final LocalDate toDate = LocalDate.parse(trip.split("to")[1].trim(), dtf);

			JollyTrip jtrip = tripRepository.findByTrip(fromDate, toDate).stream()
					.filter(o -> o.getLocation().getLocationName().equals(locationName)).collect(Collectors.toList())
					.get(0);

			JollyUser user = userRepository.findById(Long.valueOf(customer.split("-")[0].trim())).get();

			JollySchedule schedule = scheduleRepository.findByTripAndUser(jtrip, user).get();

			scheduleRepository.delete(schedule);

			// TODO Auto-generated method stub
			return true;
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return false;
		}
	}

}
