package com.heidigi.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.heidigi.domain.HeidigiUser;
import com.heidigi.jwt.JwtTokenUtil;
import com.heidigi.model.HeidigiLoginDTO;
import com.heidigi.model.HeidigiSignupDTO;
import com.heidigi.model.LoginStatusDTO;
import com.heidigi.model.ProfileDTO;
import com.heidigi.repository.HeidigiUserRepository;
import com.heidigi.service.HeidigiService;
import com.heidigi.service.JwtUserDetailsService;

@RestController
@CrossOrigin(origins = "*")
public class HeidigiController {

	@Autowired
	HeidigiService service;

	@Autowired
	HeidigiUserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	public static Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "hwlyozehf", "api_key",
			"453395666963287", "api_secret", "Q-kgBVQlRlGtdccq-ATYRFSoR8s"));

	private void authenticate(String username, String password) throws Exception {
		// System.out.println("entered in authenticate sub function...");
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
		// System.out.println("exited in authenticate sub function...");
	}

	@RequestMapping(value = "login")
	public LoginStatusDTO login(@RequestBody HeidigiLoginDTO login) {
//		return service.login(login);

		LoginStatusDTO loginStatus = new LoginStatusDTO();
		String username = login.getMobile();
		String password = login.getPassword();
		try {

			Optional<HeidigiUser> userOpt = userRepository.findByMobile(Long.valueOf(username));

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

	@RequestMapping(value = "signup")
	public Boolean signup(@RequestBody HeidigiSignupDTO signup) {
		return service.signup(signup);
	}

	@RequestMapping(value = "getImages")
	public List<String> getImages() {
		return service.getImages();
	}

	@RequestMapping(value = "getVideos")
	public List<String> getVideos() {
		return service.getVideos();
	}

//	@RequestMapping(value = "uploadLogo1")
//	public String uploadLogo1(@RequestParam("file") MultipartFile file) throws IOException {
//
//		System.out.println("came here");
//		return service.uploadLogo1(file);
//	}

	@RequestMapping(value = "editContent")
	public ProfileDTO editContent(@RequestParam("line1") String line1, @RequestParam("line2") String line2,
			@RequestParam("line3") String line3, @RequestParam("line4") String line4,
			@RequestParam("email") String email, @RequestParam("website") String website,
			@RequestParam("address") String address) throws Exception {

		return service.editContent(line1, line2, line3, line4, email, website, address);
	}

//	@RequestMapping(value = "downloadImage")
//	public String downloadImage(@RequestParam("image") String image) throws IOException {
//
//		return service.downloadImage(image);
//	}

	@RequestMapping(value = "getProfile")
	public ProfileDTO getProfile() throws Exception {

		return service.getProfile();
	}

	@RequestMapping(value = "changeTemplate")
	public ProfileDTO changeTemplate(String template) throws Exception {

		return service.changeTemplate(template);
	}

	@RequestMapping(value = "getTemplate")
	public String getTemplate(String template) throws Exception {

		return "{\"img\":\"" + service.getTemplate(template) + "\"}";
	}

	@RequestMapping(value = "uploadLogo")

	public String uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadLogo(file);
	}

	@RequestMapping(value = "uploadImage")
	public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadImage(file);
	}

	@RequestMapping(value = "uploadVideo")
	public String uploadVideo(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadVideo(file);
	}

	@RequestMapping(value = "uploadPhoto")
	public String uploadPhoto(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadPhoto(file);
	}

	@RequestMapping(value = "downloadImage")
	public String downloadImage(@RequestParam("image") String image) throws Exception {

		return service.downloadImage(image);
	}

	@RequestMapping(value = "downloadVideo")
	public String downloadVideo() throws Exception {

		return service.downloadVideo();
	}

	@RequestMapping(value = "postToFacebookImage")
	public String postToFacebookImage(@RequestParam("image") String image) throws Exception {

		return service.postToFacebookImage(image);
	}

	@RequestMapping(value = "postToFacebookVideo")
	public String postToFacebookVideo(@RequestParam("video") String video) throws Exception {

		return service.postToFacebookVideo(video);
	}

	@RequestMapping(value = "video/{tag}")
	public ResponseEntity<Object> video(@PathVariable String tag) throws Exception {

		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.downloadVideo(tag))).build();
	}

	@RequestMapping(value = "image/{tag}")
	public ResponseEntity<Object> image(@PathVariable String tag, HttpServletRequest request) throws Exception {
		
	
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.getImage(tag))).build();
	}
	
	@RequestMapping(value = "getSrc")
	public String getSrc(@RequestParam("src") String src) throws Exception {

		return service.getImage(src);
	}


}
