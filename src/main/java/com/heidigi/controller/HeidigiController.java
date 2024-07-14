package com.heidigi.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.heidigi.domain.HeidigiUser;
import com.heidigi.jwt.JwtTokenUtil;
import com.heidigi.model.DropDown;
import com.heidigi.model.HeidigiLoginDTO;
import com.heidigi.model.HeidigiSignupDTO;
import com.heidigi.model.ImageDTO;
import com.heidigi.model.LoginStatusDTO;
import com.heidigi.model.ProfileDTO;
import com.heidigi.model.SendToFacebook;
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
		;

		return loginStatus;
	}

	@RequestMapping(value = "login")
	public LoginStatusDTO login(@RequestBody HeidigiLoginDTO login) {

		return service.login(login);

	}

	@RequestMapping(value = "signup")
	public LoginStatusDTO signup(@RequestBody HeidigiSignupDTO signup) throws Exception {
		return service.signup(signup);
	}

	@RequestMapping(value = "getImages")
	public List<ImageDTO> getImages() {
		return service.getImages();
	}

	@RequestMapping(value = "getVideos")
	public List<ImageDTO> getVideos() {
		return service.getVideos();
	}

	@RequestMapping(value = "editContent")
	public ProfileDTO editContent(@RequestParam("line1") String line1, @RequestParam("line2") String line2,
			@RequestParam("line3") String line3, @RequestParam("line4") String line4,
			@RequestParam("email") String email, @RequestParam("website") String website,
			@RequestParam("address") String address) throws Exception {

		return service.editContent(line1, line2, line3, line4, email, website, address);
	}

	@RequestMapping(value = "getProfile")
	public ProfileDTO getProfile() throws Exception {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println("username :: " + userDetails.getUsername());
		return service.getProfile();
	}

	@RequestMapping(value = "getFacebookPageNames")
	public List<String> getFacebookPageNames() throws Exception {
		return service.getFacebookPageNames();
	}

	@RequestMapping(value = "getInstagramPageNames")
	public List<String> getInstagramPageNames() throws Exception {
		return service.getInstagramPageNames();
	}

	@RequestMapping(value = "uploadLogo")

	public String uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadLogo(file);
	}

	@RequestMapping(value = "checkProfile")

	public Boolean checkProfile() throws Exception {
		return service.checkProfile();
	}

	@RequestMapping(value = "uploadImage")
	public String uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("category") String category,
			@RequestParam("subCategory") String subCategory, String tags) throws Exception {
		return service.uploadImage(file, category, subCategory, tags);
	}

	@RequestMapping(value = "uploadVideo")
	public String uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("category") String category,
			@RequestParam("subCategory") String subCategory) throws Exception {
		return service.uploadVideo(file, category, subCategory);
	}

	@RequestMapping(value = "uploadPhoto")
	public String uploadPhoto(@RequestParam("file") MultipartFile file) throws Exception {
		return service.uploadPhoto(file);
	}

	@RequestMapping(value = "downloadImage")
	public String downloadImage(@RequestParam("image") String image, @RequestParam("template") String template)
			throws Exception {

		return service.downloadImage(image, template);
	}

	@RequestMapping(value = "downloadVideo")
	public byte[] downloadVideo(@RequestParam("video") String video, @RequestParam("template") String template,
			HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=demo.mp4");
		String url = service.downloadVideo(video, template, false);

		System.out.println(url);

		byte[] responseSend = new RestTemplate().getForObject(url, byte[].class);

		return responseSend;

	}

	@RequestMapping(value = "postToFacebookImage")
	public String postToFacebookImage(@RequestBody SendToFacebook send) throws Exception {

		return service.postToFacebookImage(send.getImage(), send.getTemplate(), send.getPages());
	}

	@RequestMapping(value = "postToInstagramImage")
	public String postToInstagramImage(@RequestBody SendToFacebook send) throws Exception {

		return service.postToInstagramImage(send.getImage(), send.getTemplate(), send.getPages());
	}

	@RequestMapping(value = "postToInstagramVideo")
	public String postToInstagramVideo(@RequestBody SendToFacebook send) throws Exception {

		return service.postToInstagramVideo(send.getImage(), send.getTemplate(), send.getPages());
	}

	@RequestMapping(value = "saveFacebookToken")
	public Boolean saveFacebookToken(@RequestParam("accessToken") String accessToken) throws Exception {

		return service.saveFacebookToken(accessToken);
	}

	@RequestMapping(value = "getCategories")
	public List<DropDown> getCategories() {

		return service.getCategories();
	}

	@RequestMapping(value = "getCategory")
	public String getCategory() throws Exception {

		return "{\"name\":\"" + service.getCategory() + "\"}";
	}

	@RequestMapping(value = "getSubCategories")
	public List<DropDown> getSubCategories(@RequestParam("category") String category) {

		return service.getSubCategories(category);
	}

	@RequestMapping(value = "facebookToken")
	public Boolean facebookToken() throws Exception {

		return service.facebookToken();
	}

	@RequestMapping(value = "postToFacebookVideo")
	public String postToFacebookVideo(@RequestBody SendToFacebook send) throws Exception {

		return service.postToFacebookVideo(send.getImage(), send.getTemplate(), send.getPages());
	}

	@RequestMapping(value = "video/{tag}")
	public ResponseEntity<Object> video(@PathVariable String tag) throws Exception {

		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.getVideo(tag))).build();
	}

	@RequestMapping(value = "image/{tag}")
	public ResponseEntity<Object> image(@PathVariable String tag, HttpServletRequest request) throws Exception {

		System.out.println(request.getHeader("Referer"));
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.getImage(tag))).build();
	}

	@RequestMapping(value = "showTemplate")
	public List<Object> showTemplate(@RequestParam("image") String image) throws Exception {

		List<Object> templates = new ArrayList<>();
		templates.add("{\"img\":\"" + image + "\"}");
		templates.add(service.showTemplate(image, "Template 1"));
		templates.add(service.showTemplate(image, "Template 2"));

		return templates;
	}

	@RequestMapping(value = "showTemplateVideo")
	public List<Object> showTemplateVideo(@RequestParam("video") String video) throws Exception {

		List<Object> templates = new ArrayList<>();
		templates.add("{\"video\":\"" + video + "\"}");
		templates.add(service.downloadVideo(video, "Template 1", false));
		templates.add(service.downloadVideo(video, "Template 2", false));

		return templates;
	}

	@RequestMapping(value = "getSrc")
	public String getSrc(@RequestParam("src") String src) throws Exception {

		return service.getImage(src);
	}

	@RequestMapping(value = "generateOTP")
	public Boolean generateOTP(@RequestParam("mobile") String mobile) throws Exception {

		return service.generateOTP(mobile);
	}

	@RequestMapping(value = "facebookLogin")
	public LoginStatusDTO facebookLogin(@RequestParam("accessToken") String accessToken) throws Exception {

		return service.facebookLogin(accessToken);
	}
	
	@RequestMapping(value = "facebookSignup")
	public LoginStatusDTO facebookSignup(@RequestParam("accessToken") String accessToken, @RequestParam("role") String role, @RequestParam("category") String category) throws Exception {

		return service.facebookSignup(accessToken, role, category);
	}

	@RequestMapping(value = "reIntegrateFacebook")
	public Boolean reIntegrateFacebook() throws Exception {

		return service.reIntegrateFacebook();
	}

}
