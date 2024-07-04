package com.heidigi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.transformation.Layer;
import com.cloudinary.transformation.TextLayer;
import com.cloudinary.utils.ObjectUtils;
import com.heidigi.domain.AuditTrail;
import com.heidigi.domain.HeidigiImage;
import com.heidigi.domain.HeidigiProfile;
import com.heidigi.domain.HeidigiUser;
import com.heidigi.domain.HeidigiVideo;
import com.heidigi.jwt.JwtTokenUtil;
import com.heidigi.model.Datum;
import com.heidigi.model.DropDown;
import com.heidigi.model.FacebookDTO;
import com.heidigi.model.FacebookPage;
import com.heidigi.model.FacebookVideoDTO;
import com.heidigi.model.FacebookVideoPublishDTO;
import com.heidigi.model.HeidigiLoginDTO;
import com.heidigi.model.HeidigiSignupDTO;
import com.heidigi.model.ImageDTO;
import com.heidigi.model.InstaCIDDTO;
import com.heidigi.model.InstaDTO;
import com.heidigi.model.InstagramDTO;
import com.heidigi.model.InstagramPage;
import com.heidigi.model.LoginStatusDTO;
import com.heidigi.model.ProfileDTO;
import com.heidigi.repository.AuditRepository;
import com.heidigi.repository.CategoryRepository;
import com.heidigi.repository.HeidigiImageRepository;
import com.heidigi.repository.HeidigiProfileRepository;
import com.heidigi.repository.HeidigiRoleRepository;
import com.heidigi.repository.HeidigiUserRepository;
import com.heidigi.repository.HeidigiVideoRepository;
import com.heidigi.repository.SubCategoryRepository;

@Service
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HeidigiService {

	@Value("${spring.social.facebook.appId}")
	String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	String facebookSecret;

	@Lazy
	@Autowired
	private HeidigiService hService;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	HeidigiImageRepository heidigiImageRepository;

	@Autowired
	AuditRepository auditRepository;

	@Autowired
	HeidigiVideoRepository heidigiVideoRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	SubCategoryRepository subCategoryRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	static RestTemplate restTemplate = new RestTemplate();

	@Autowired
	HeidigiUserRepository userRepository;
	@Autowired
	HeidigiRoleRepository roleRepository;
	@Autowired
	HeidigiProfileRepository profileRepository;

	public static Cloudinary cloudinary1 = new Cloudinary(ObjectUtils.asMap("cloud_name", "hwlyozehf", "api_key",
			"453395666963287", "api_secret", "Q-kgBVQlRlGtdccq-ATYRFSoR8s"));

	public static Cloudinary cloudinary2 = new Cloudinary(ObjectUtils.asMap("cloud_name", "hu4jsyyt8", "api_key",
			"491845868955893", "api_secret", "oYgotm7eQgCcLzffOoo7oHPJ874"));

	public static Cloudinary cloudinary[] = { cloudinary1, cloudinary2 };

	public LoginStatusDTO facebookSignup(String accessToken, String role, String category) throws Exception {

		String url = "https://graph.facebook.com/v20.0/me?access_token=" + accessToken
				+ "&debug=all&fields=id,name,email&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";

		FacebookPage page = restTemplate.getForObject(url, FacebookPage.class);

		HeidigiSignupDTO signup = new HeidigiSignupDTO();

		signup.setCategory(category);
		signup.setEmail(page.getEmail());
		signup.setMobile(page.getId());
		signup.setName(page.getName());
		signup.setRole(role);
		return signup(signup);
	}

	public LoginStatusDTO signup(HeidigiSignupDTO signup) throws Exception {

		LoginStatusDTO loginStatus = new LoginStatusDTO();

		Optional<HeidigiUser> userOpt = userRepository.findByMobile(Long.valueOf(signup.getMobile()));

		Optional<HeidigiUser> userOpt1 = userRepository.findByEmail(signup.getEmail());

		System.out.println("in singup");

		if (!userOpt.isPresent() && !userOpt1.isPresent()) {
			System.out.println("in singup2");
			HeidigiUser user = new HeidigiUser();
			user.setEmail(signup.getEmail());
			user.setMobile(Long.valueOf(signup.getMobile()));
			user.setName(signup.getName());
			user.setPassword(generateOTP(4));
			user.setMessage("Customer Signup");
			user.setRole(
					roleRepository.findByRoleName(signup.getRole().equals("Business") ? "Customer" : "Designer").get());
			user.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
			user.setIsDeleted(false);
			user.setIsDisabled(false);
			if (categoryRepository.findByCname(signup.getCategory()).isPresent())
				user.setCategory(categoryRepository.findByCname(signup.getCategory()).get());
			userRepository.save(user);
			loginStatus.setLoginStatus(
					userRepository.findByMobileAndPassword(user.getMobile(), user.getPassword()).isPresent());
			loginStatus.setMessage("Login Successful");

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

	public String uploadImage(MultipartFile file, String category, String subCategory, String tags) throws Exception {
		uploadImage(file, category, subCategory, "Image", tags);
		return "{\"result\":\"success\"}";
	}

	public Long getUserName() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return Long.valueOf(userDetails.getUsername());
	}

	public String getRole() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (userDetails.getAuthorities().toArray()[0].toString());
	}

	public String uploadVideo(MultipartFile file, String category, String subCategory) throws Exception {

		System.out.println("In video upload " + new Date());
		File convFile = new File(UUID.randomUUID() + "" + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		System.out.println("In video done " + new Date());

		Map uploadResult1 = cloudinary1.uploader().upload(convFile, ObjectUtils.asMap("resource_type", "video"));

		System.out.println("In video uploading " + new Date());

		HeidigiVideo image = new HeidigiVideo();

		image.setPublicId(uploadResult1.get("public_id") + "");

		image.setResponse(uploadResult1.toString());
		image.setCategory(category);
		image.setSubcategory(subCategory);

		image.setUser(userRepository.findByMobile(getUserName()).get());

		heidigiVideoRepository.save(image);

		System.out.println("In video saving " + new Date());

		return "{\"result\":\"success\"}";
	}

	public HeidigiImage uploadImage(MultipartFile file, String category, String subCategory, String type, String tags)
			throws Exception {

		File convFile = new File(UUID.randomUUID() + "" + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

//		InputStream is = new ByteArrayInputStream(file.getBytes());
//		BufferedImage img = ImageIO.read(is);
//
//		if (!type.equals("Logo"))
//			img = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, 300, 300);
//
//		String extension = file.getOriginalFilename().split("\\.")[file.getOriginalFilename().split("\\.").length - 1]
//				.toLowerCase();
//
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		if (extension.toLowerCase().equals("jpg"))
//			ImageIO.write(img, "JPEG", bos);
//		else if (extension.toLowerCase().equals("png"))
//			ImageIO.write(img, "PNG", bos);
//
//		String imageText = new String(Base64.encodeBase64(bos.toByteArray()), "UTF-8");
//
//		System.out.println(imageText);

		Map uploadResult1 = cloudinary1.uploader().upload(convFile, ObjectUtils.emptyMap());

		HeidigiImage image = new HeidigiImage();
		image.setCategory(category);
		image.setSubcategory(subCategory);
		if (subCategoryRepository.findByName(subCategory).isPresent())
			image.setSubCat(subCategoryRepository.findByName(subCategory).get());

		image.setType(type);
		image.setPublicId(uploadResult1.get("public_id") + "");
		image.setTags(tags);
		image.setResponse(uploadResult1.toString());

//		image.setExtension(extension);
		image.setUser(userRepository.findByMobile(getUserName()).get());
//		image.setImageText(imageText);

		heidigiImageRepository.save(image);

		System.out.println("Backup started");

		hService.backupUploadedImage(convFile, image);

		System.out.println("Backup Ended");

		return image;
	}

	@Async
	public void backupUploadedImage(File convFile, HeidigiImage image) throws Exception {

		System.out.println("In Backup started");
		Map uploadResult2 = cloudinary2.uploader().upload(convFile, ObjectUtils.emptyMap());

		image.setBackupPublicId(uploadResult2.get("public_id") + "");

		image.setBackupResponse(uploadResult2.toString());

		heidigiImageRepository.save(image);
		System.out.println("In Backup ended");
	}

	public String uploadLogo(MultipartFile file) throws Exception {

		HeidigiProfile profile = null;
		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		if (!profileOpt.isPresent()) {
			profile = new HeidigiProfile();

			profile.setUser(userRepository.findByMobile(getUserName()).get());
		} else
			profile = profileOpt.get();

		profile.setLogo(uploadImage(file, "Logo", "Logo", "Logo", ""));
		profileRepository.save(profile);

		return "";
	}

	public String uploadPhoto(MultipartFile file) throws Exception {

		HeidigiProfile profile = null;
		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		if (!profileOpt.isPresent()) {
			profile = new HeidigiProfile();

			profile.setUser(userRepository.findByMobile(getUserName()).get());
		} else
			profile = profileOpt.get();

		profile.setPhoto(uploadImage(file, "Photo", "Photo", "Photo", ""));
		profileRepository.save(profile);

		return "";

	}

	public ProfileDTO editAddress(String address) throws Exception {

		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		HeidigiProfile profile = null;

		if (!profileOpt.isPresent()) {
			profile = new HeidigiProfile();
			profile.setUser(userRepository.findByMobile(getUserName()).get());
		} else
			profile = profileOpt.get();
		System.out.println(address);
		profile.setAddress(address);
		profileRepository.save(profile);

		return getProfile();
	}

	public List<ImageDTO> getImages() {

		String category = "";

		if (getRole().equals("Customer"))
			category = userRepository.findByMobile(getUserName()).get().getCategory().getCname();

		System.out.println("Category is " + category);

		List<HeidigiImage> images = heidigiImageRepository.getImageIds(getUserName(), getRole(), category);
		return images.stream().map(o -> new ImageDTO(o)).collect(Collectors.toList());
	}

	public List<DropDown> getCategories() {

		return categoryRepository.findAll().stream().map(o -> new DropDown(o.getCname(), o.getCname()))
				.collect(Collectors.toList());
	}

	public List<DropDown> getSubCategories(String category) {

		return subCategoryRepository.findAll().stream().filter(o -> o.getCategory().getCname().equals(category))
				.map(o -> new DropDown(o.getName(), o.getName())).collect(Collectors.toList());
	}

	public List<ImageDTO> getVideos() {

		return heidigiVideoRepository.getVideos(getUserName(), getRole()).stream().map(o -> new ImageDTO(o))
				.collect(Collectors.toList());

	}

	public Boolean checkProfile() throws Exception {
		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		if ((profileOpt.isPresent() && profileOpt.get().getAddress().trim().length() != 0
				&& profileOpt.get().getAddress().trim().length() != 0
				&& profileOpt.get().getLine1().trim().length() != 0 && profileOpt.get().getLine2().trim().length() != 0
				&& profileOpt.get().getLine3().trim().length() != 0 && profileOpt.get().getLine4().trim().length() != 0
				&& profileOpt.get().getEmail().trim().length() != 0
				&& profileOpt.get().getWebsite().trim().length() != 0 && profileOpt.get().getLogo() != null
				&& profileOpt.get().getLogo().getPublicId() != null && profileOpt.get().getPhoto() != null
				&& profileOpt.get().getPhoto().getPublicId() != null) || getRole().equals("Designer"))
			return true;

		else

			return false;
	}

	public ProfileDTO getProfile() throws Exception {

		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());

		String category = getCategory();
		// getRole().equals("Business")?userRepository.findByMobile(getUserName()).get().getCategory().getCname():"";

		ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setCategory(category);

		if (profileOpt.isPresent()) {
			HeidigiProfile profile = profileOpt.get();

			profileDTO.setAddress(profile.getAddress());

			if (profile.getLogo() != null && profile.getLogo().getPublicId() != null)
				profileDTO.setLogo(profile.getLogo().getPublicId());
			else
				profileDTO.setLogo("hu8doewfg7syktb1xo8l");
			if (profile.getPhoto() != null && profile.getPhoto().getPublicId() != null)
				profileDTO.setPhoto(profile.getPhoto().getPublicId());
			else
				profileDTO.setPhoto("hu8doewfg7syktb1xo8l");
			profileDTO.setMobile(profile.getUser().getMobile() + "");
			profileDTO.setEmail(profile.getEmail());
			profileDTO.setWebsite(profile.getWebsite());
			profileDTO.setLine1(profile.getLine1());
			profileDTO.setLine2(profile.getLine2());
			profileDTO.setLine3(profile.getLine3());
			profileDTO.setLine4(profile.getLine4());

		} else {

			profileDTO.setLogo("hu8doewfg7syktb1xo8l");
			profileDTO.setPhoto("hu8doewfg7syktb1xo8l");

		}
		return profileDTO;
	}

	public String getImageUrl(String image, Boolean template, Boolean watermark) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();
		String logoId = profile.getLogo().getPublicId();

		String photoId = profile.getPhoto().getPublicId();

		String line1 = profile.getLine1();
		String line2 = profile.getLine2();
		String line3 = profile.getLine3();
		String line4 = profile.getLine4();

		String email = profile.getEmail();
		String address = profile.getAddress();
		String website = profile.getWebsite();

		System.out.println(logoId);

		Transformation transformation = new Transformation();

		if (template)
			transformation = transformation.effect("blur:700");

		transformation = transformation.height(1080).width(1080).crop("scale").chain()

				// logo
				.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative").gravity("north_west")
				.opacity(100).height(0.15).x(10).y(10).crop("scale").chain();

		if (watermark)
			transformation = transformation.overlay(new Layer().publicId("mvj11zgltg9mqjgy7z4d")).chain()
					.flags("layer_apply", "relative").gravity("north_west").opacity(20).radius(30).width(1080)
					.height(1080).x(0).y(0).crop("scale").chain();

		transformation = transformation.overlay(new Layer().publicId("akdvbdniqfbncjrapghb")).chain()
				.flags("layer_apply", "relative").gravity("south_west").width(0.65).height(0.18).opacity(100).chain()

				// 65% bottom background
				.overlay(new Layer().publicId("akdvbdniqfbncjrapghb")).chain().flags("layer_apply", "relative")
				.gravity("south_west").width(0.65).height(0.18).opacity(100).chain()

				// 35% bottom background
				.overlay(new Layer().publicId("tff8vf9ciycuste9iupb")).chain().flags("layer_apply", "relative")
				.gravity("south_east").width(0.35).height(0.18).opacity(100).chain()

				// icon1: Envelope
				.overlay(new Layer().publicId("dt7fah8qrkeleor3gpq3")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(110).chain()

				// icon2: Internet Globe
				.overlay(new Layer().publicId("rnxve3ik0plwyrvh3whh")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(80).chain()

				// icon3: Red Map Marker
				.overlay(new Layer().publicId("b5dnqxn9rd21wpekin6w")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(50).chain()

				// Person Photo
				.overlay(new Layer().publicId(photoId)).aspectRatio("1.0").gravity("faces").width(0.5).zoom(0.7)
				.crop("thumb").chain().flags("layer_apply", "relative").gravity("south_west").opacity(100).radius("max")
				.width(0.15).x(10).y(15).crop("scale").chain()

				// Text: Line 1
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(30).fontWeight("bold").textAlign("center")
						.text(line1))
				.flags("layer_apply", "relative").gravity("south_west").x(200).y(120).color("white").chain()

				// Text: Line 2
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(15).textAlign("center").text(line2))
				.gravity("south_west").x(200).y(90).color("white").chain()

				// Text: Line 3
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(15).textAlign("center").text(line3))
				.gravity("south_west").x(200).y(65).color("white").chain()

				// Text: Line 4
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(18).fontWeight("bold").textAlign("center")
						.text(line4))
				.gravity("south_west").x(200).y(37).color("white").chain()

				// Text: Mail
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(email))
				.gravity("south_west").x(750).y(110).color("black").chain()

				// Text: Website
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(website))
				.gravity("south_west").x(750).y(80).color("black").chain()

				// Text: Address
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(address))
				.gravity("south_west").x(750).y(50).color("black").chain();

		String imageUrl = cloudinary1.url().transformation(transformation).imageTag(image + ".jpg");
		imageUrl = imageUrl.substring(10, imageUrl.length() - 3);

		System.out.println(imageUrl);

		return imageUrl;
	}

	public String getImage(String image) {
		return "https://res.cloudinary.com/hwlyozehf/image/upload/" + image + ".jpg";

	}

	public String getVideo(String video) {
		return "https://res.cloudinary.com/hwlyozehf/video/upload/" + video + ".mp4";

	}

	public String getImageUrlTemplate2(String image, Boolean template, Boolean watermark) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String logoId = profile.getLogo().getPublicId();

		String address = profile.getAddress();
		String website = profile.getWebsite();

		System.out.println(logoId);

		Transformation transformation = new Transformation();

		if (template)
			transformation = transformation.effect("blur:700");

		transformation = transformation.height(1080).width(1080).crop("scale").chain()

				// logo
				.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative").gravity("north_east")
				.opacity(100).height(0.15).x(10).y(10).crop("scale").chain();

		if (watermark)

			transformation = transformation.overlay(new Layer().publicId("mvj11zgltg9mqjgy7z4d")).chain()
					.flags("layer_apply", "relative").gravity("north_west").opacity(20).radius(30).width(1080)
					.height(1080).x(0).y(0).crop("scale").chain();

		transformation = transformation.overlay(new Layer().publicId("v6s3p850kn4aozfltfjd")).chain()
				.flags("layer_apply", "relative").gravity("south").width(1).height(0.04).y(30).opacity(50).chain()

				.overlay(new TextLayer().fontFamily("montserrat").fontSize(25).textAlign("center")
						.text("☎ 9449 840 144 | ☸ " + website + " | ⚲ " + address))
				.flags("layer_apply", "relative").gravity("south").y(35).color("white").chain();

		String imageUrl = cloudinary1.url().transformation(transformation).imageTag(image + ".jpg");

		imageUrl = imageUrl.substring(10, imageUrl.length() - 3);

		return imageUrl;
	}

	public String downloadImage(String image, String template) throws Exception {

		String imageUrl = "";

		if (template.equals("Template 1"))
			imageUrl = URLDecoder.decode(getImageUrl(image, false, false), "UTF-8");
		else
			imageUrl = URLDecoder.decode(getImageUrlTemplate2(image, false, false), "UTF-8");

		System.out.println("Download :: " + imageUrl);
		String imageStr = getImage(imageUrl, false);
		return imageStr;

//		if (template.equals("Template 1"))
//			imageUrl = getImageUrl(image, false, true);
//		else
//			imageUrl = getImageUrlTemplate2(image, false, true);
//
//		return "{\"img\":\"" +imageUrl+"\"}";

	}

	public String showTemplate(String image, String template) throws Exception {

		String imageUrl = "";

//		if (template.equals("Template 1"))
//			imageUrl = URLDecoder.decode(getImageUrl(image, false,true), "UTF-8");
//		else
//			imageUrl = URLDecoder.decode(getImageUrlTemplate2(image, false, true), "UTF-8");
//
//		System.out.println("Download :: " + imageUrl);
//		String imageStr = getImage(imageUrl, false);
//		return imageStr;

		if (template.equals("Template 1"))
			imageUrl = getImageUrl(image, false, true);
		else
			imageUrl = getImageUrlTemplate2(image, false, true);

		return "{\"img\":\"" + imageUrl + "\"}";

	}

	public String getImage(String url, Boolean template) {

		try {

			byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

			String image = new String(Base64.encodeBase64(imageBytes), "UTF-8");

			return "{\"img\":\"" + "data:image/jpeg;base64," + image + "\"}";

		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}

	}

	public byte[] getImageString(String url) {

		try {

			byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

			return imageBytes;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public ProfileDTO editContent(String line1, String line2, String line3, String line4, String email, String website,
			String address) throws Exception {

		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		HeidigiProfile profile = null;

		if (!profileOpt.isPresent()) {
			profile = new HeidigiProfile();
			profile.setUser(userRepository.findByMobile(getUserName()).get());
		} else
			profile = profileOpt.get();

		profile.setAddress(address);
		profile.setLine1(line1);
		profile.setLine2(line2);
		profile.setLine3(line3);
		profile.setLine4(line4);
		profile.setEmail(email);
		profile.setWebsite(website);

		profileRepository.save(profile);

		return getProfile();
	}

	public String downloadVideo(String publicId, String template, Boolean instagram) throws Exception {
		if (template.equals("Template 1"))
			return downloadVideo(publicId, instagram);
		else
			return downloadVideoTemplate2(publicId, instagram);

	}

	public String downloadVideo(String publicId, Boolean instagram) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String logoId = profile.getLogo().getPublicId();

		String photoId = profile.getPhoto().getPublicId();

		String line1 = profile.getLine1();
		String line2 = profile.getLine2();
		String line3 = profile.getLine3();
		String line4 = profile.getLine4();

		String email = profile.getEmail();
		String address = profile.getAddress();
		String website = profile.getWebsite();

		System.out.println(logoId);

		Transformation transformation = new Transformation();

		int height = 1080, width = 1080;
		double logoHeight = 0.15d, layerHeight = 0.18d;
		if (instagram) {
			height = 1920;
			logoHeight = 0.08d;
			layerHeight = 0.105d;
		}

		String videoUrl = cloudinary1.url().transformation(transformation.height(height).width(width).crop("scale")
				.chain()

				// logo
				.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative").gravity("north_west")
				.opacity(100).height(logoHeight).x(10).y(10).crop("scale").chain()

				// 65% bottom background
				.overlay(new Layer().publicId("akdvbdniqfbncjrapghb")).chain().flags("layer_apply", "relative")
				.gravity("south_west").width(0.65).height(layerHeight).opacity(100).chain()

				// 35% bottom background
				.overlay(new Layer().publicId("tff8vf9ciycuste9iupb")).chain().flags("layer_apply", "relative")
				.gravity("south_east").width(0.35).height(layerHeight).opacity(100).chain()

				// icon1: Envelope
				.overlay(new Layer().publicId("dt7fah8qrkeleor3gpq3")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(110).chain()

				// icon2: Internet Globe
				.overlay(new Layer().publicId("rnxve3ik0plwyrvh3whh")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(80).chain()

				// icon3: Red Map Marker
				.overlay(new Layer().publicId("b5dnqxn9rd21wpekin6w")).width(20).height(20).chain()
				.flags("layer_apply", "relative").gravity("south_east").x(340).y(50).chain()

				// Person Photo
				.overlay(new Layer().publicId(photoId)).aspectRatio("1.0").gravity("faces").width(0.5).zoom(0.7)
				.crop("thumb").chain().flags("layer_apply", "relative").gravity("south_west").opacity(100).width(0.15)
				.x(10).y(15).crop("scale").chain()

				// Text: Line 1
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(30).fontWeight("bold").textAlign("center")
						.text(line1))
				.flags("layer_apply", "relative").gravity("south_west").x(200).y(120).color("white").chain()

				// Text: Line 2
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(15).textAlign("center").text(line2))
				.gravity("south_west").x(200).y(90).color("white").chain()

				// Text: Line 3
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(15).textAlign("center").text(line3))
				.gravity("south_west").x(200).y(65).color("white").chain()

				// Text: Line 4
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(18).fontWeight("bold").textAlign("center")
						.text(line4))
				.gravity("south_west").x(200).y(37).color("white").chain()

				// Text: Mail
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(email))
				.gravity("south_west").x(750).y(110).color("black").chain()

				// Text: Website
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(website))
				.gravity("south_west").x(750).y(80).color("black").chain()

				// Text: Address
				.overlay(new TextLayer().fontFamily("montserrat").fontSize(16).textAlign("center").text(address))
				.gravity("south_west").x(750).y(50).color("black").chain()

		).videoTag(publicId + ".mp4");

		List<String> urls = Arrays.asList(videoUrl.split("<source src='"));

		return urls.stream().filter(o -> o.indexOf(".mp4") != -1).map(o -> o.substring(0, o.indexOf(".mp4") + 4))
				.collect(Collectors.toList()).get(0);

		// return imageUrl;

	}

	public String downloadVideoTemplate2(String video, Boolean instagram) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String logoId = profile.getLogo().getPublicId();

		String address = profile.getAddress();
		String website = profile.getWebsite();

		System.out.println(logoId);

		int height = 1080, width = 1080;
		double logoHeight = 0.15d, layerHeight = 0.04d;
		if (instagram) {
			height = 1920;
			logoHeight = 0.08d;
			layerHeight = 0.03d;
		}

		Transformation transformation = new Transformation();

		String videoUrl = cloudinary1.url()
				.transformation(transformation.height(height).width(width).crop("scale").chain()

						// logo
						.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative")
						.gravity("north_east").opacity(100).height(logoHeight).x(10).y(10).crop("scale")
						.chain()

						// 100% bottom background
						.overlay(new Layer().publicId("v6s3p850kn4aozfltfjd")).chain().flags("layer_apply", "relative")
						.gravity("south").width(1).height(layerHeight).y(40).opacity(100).chain()

						.overlay(new TextLayer().fontFamily("montserrat").fontSize(25).textAlign("center")
								.text("☎ 9449 840 144 | ☸ " + website + " | ⚲ " + address))
						.flags("layer_apply", "relative").gravity("south").y(50).color("white").chain()

				).videoTag(video + ".mp4");

		List<String> urls = Arrays.asList(videoUrl.split("<source src='"));

		return urls.stream().filter(o -> o.indexOf(".mp4") != -1).map(o -> o.substring(0, o.indexOf(".mp4") + 4))
				.collect(Collectors.toList()).get(0);

	}

	public Boolean reIntegrateFacebook() {
		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String accessToken = profile.getFacebookToken();

		if (accessToken != null) {

			String url = "https://graph.facebook.com/v20.0/me?access_token=" + accessToken
					+ "&debug=all&fields=id,name,accounts&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";
			FacebookPage fpage = restTemplate.getForObject(url, FacebookPage.class);

			String test = new RestTemplate().exchange(
					"https://graph.facebook.com/" + fpage.getId() + "/permissions?access_token=" + accessToken,
					HttpMethod.DELETE, null, String.class).getBody();
			if (test.toLowerCase().indexOf("true") != -1) {
				profile = profileRepository.findByMobile(getUserName()).get();
				profile.setFacebookToken(null);

				profileRepository.save(profile);

			}
		}
		return true;
	}

	public String postToFacebookVideo(String video, String template, List<String> pages) throws Exception {

		System.out.println(video + " " + template);

		FacebookVideoDTO fdto = new FacebookVideoDTO();
//		fdto.setMessage("This is Testing");

		String videoUrl = downloadVideo(video, template, true);

		fdto.setUpload_phase("start");

		for (int i = 0; i < pages.size(); i++) {
			String page = pages.get(i);
			String accessToken = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getAccess_token();
			fdto.setAccess_token(accessToken);

			String pageId = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getId();

			FacebookVideoDTO result = new RestTemplate()
					.postForEntity("https://graph.facebook.com/v20.0/" + pageId + "/video_reels", fdto, FacebookVideoDTO.class)
					.getBody();
			
//			FacebookDTO param=new FacebookDTO();
//			param.setAccess_token(accessToken);
//			param.setFile_url(videoUrl);
			HttpHeaders headers = new HttpHeaders();
			
//	        headers.add("Authorization", "Basic " + getBase64Credentials());
			headers. set("Accept", "application/json"); 
	        headers.set("Authorization", "OAuth "+accessToken);
	        headers.set("file_url", videoUrl.replaceAll(Pattern.quote("%"), "%25"));
			
			
	        HttpEntity<String> entity = new HttpEntity<>( headers);
			
			System.out.println(result+"\n"+entity);
			
			String result1 = new RestTemplate()
					.postForEntity( result.getUpload_url(), entity, String.class)
					.getBody();

			System.out.println(result1);
			
			
			FacebookVideoPublishDTO publish=new  FacebookVideoPublishDTO();
			publish.setAccess_token(accessToken);
			publish.setDescription("This is Testing");
			publish.setUpload_phase("finish");
			publish.setVideo_id(result.getVideo_id());
			publish.setVideo_state("PUBLISHED");
			
			String result2 = new RestTemplate()
					.postForEntity( "https://graph.facebook.com/v20.0/" + pageId + "/video_reels", publish, String.class)
					.getBody();

			System.out.println(result2);
			
			AuditTrail audit = new AuditTrail();
			audit.setUser(userRepository.findByMobile(getUserName()).get());
			audit.setLine1("Posted to Facebook");
			audit.setLine2(accessToken);
			audit.setLine3(pageId);
			audit.setLine4(result.getVideo_id());

			auditRepository.save(audit);
		}
		return "";

	}

	public List<Datum> getFacebookPageDetails() throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String accessToken = profile.getFacebookToken();

		String url = "https://graph.facebook.com/v20.0/me?access_token=" + accessToken
				+ "&debug=all&fields=id,name,accounts&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";
		FacebookPage fpage = restTemplate.getForObject(url, FacebookPage.class);

		return fpage.getAccounts().getData();

	}

	public List<InstagramPage> getInstagramAccountDetails() throws Exception {

		List<InstagramPage> instagramPages = getFacebookPageDetails().stream().map(o -> {
			try {
				return getInstagramBusinessAccountDetails(o.getId());
			} catch (Exception e) {
				e.printStackTrace();
				return new InstagramPage();

			}
		}).collect(Collectors.toList());

		System.out.println(instagramPages);

		return instagramPages;
	}

	public InstagramPage getInstagramBusinessAccountDetails(String facebookId) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String accessToken = profile.getFacebookToken();

		String url = "https://graph.facebook.com/v20.0/" + facebookId + "?access_token=" + accessToken
				+ "&debug=all&fields=instagram_business_account,name&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";

		System.out.println("Take this :: " + url);

		InstagramPage ipage = restTemplate.getForObject(url, InstagramPage.class);

		if (ipage.getInstagram_business_account() != null) {

			url = "https://graph.facebook.com/v20.0/" + ipage.getId() + "?access_token=" + accessToken
					+ "&debug=all&fields=name&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";

			System.out.println("Take this1 :: " + url);

			InstagramPage bpage = restTemplate.getForObject(url, InstagramPage.class);

			ipage.setName(bpage.getName());
		} else {
			ipage = null;
		}
		System.out.println("use this :: " + ipage);
		return ipage;

	}

	public List<String> getInstagramPageNames() throws Exception {

		return getInstagramAccountDetails().stream().filter(o -> o != null).map(o -> o.getName())
				.collect(Collectors.toList());
	}

	public List<String> getFacebookPageNames() throws Exception {
		return getFacebookPageDetails().stream().map(o -> o.getName()).sorted().collect(Collectors.toList());
	}

	public static File getResourceAsFile(String relativeFilePath) throws FileNotFoundException {
		return ResourceUtils.getFile(String.format("classpath:%s", relativeFilePath));

	}

	public String postToInstagramImage(String image, String template, List<String> pages) throws Exception {

		InstagramDTO fdto = new InstagramDTO();
		fdto.setCaption("This is Testing");

		String imageUrl = "";

		if (template.equals("Template 1"))
			imageUrl = getImageUrl(image, false, false);
		else
			imageUrl = getImageUrlTemplate2(image, false, false);

		fdto.setImage_url(imageUrl.replaceAll(Pattern.quote("%"), "%25"));

		System.out.println(imageUrl.replaceAll(Pattern.quote("%"), "%25"));

		for (int i = 0; i < pages.size(); i++) {
			String page = pages.get(i);
			String accessToken = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getAccess_token();
			fdto.setAccess_token(accessToken);

			String pageId = getInstagramAccountDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getInstagram_business_account().getId();

			System.out.println(pageId);

			InstaDTO result = new RestTemplate()
					.postForEntity("https://graph.facebook.com/v20.0/" + pageId + "/media", fdto, InstaDTO.class)
					.getBody();

			System.out.println(result.getId());

			InstaCIDDTO cidDTO = new InstaCIDDTO();
			cidDTO.setCreation_id(result.getId());
			cidDTO.setAccess_token(accessToken);

			InstaDTO result1 = new RestTemplate().postForEntity(
					"https://graph.facebook.com/v20.0/" + pageId + "/media_publish", cidDTO, InstaDTO.class).getBody();

			System.out.println(result1.getId());

			AuditTrail audit = new AuditTrail();
			audit.setUser(userRepository.findByMobile(getUserName()).get());
			audit.setLine1("Posted to Instagram");
			audit.setLine2(accessToken);
			audit.setLine3(pageId);
			audit.setLine4(result + "");

			auditRepository.save(audit);
		}
		return "";
	}

	public String postToInstagramVideo(String image, String template, List<String> pages) throws Exception {

		System.out.println(image + " " + template);

		InstagramDTO fdto = new InstagramDTO();
		fdto.setCaption("This is Testing");

		String videoUrl = downloadVideo(image, template, true);

		fdto.setVideo_url(videoUrl.replaceAll(Pattern.quote("%"), "%25"));
		fdto.setMedia_type("REELS");

		System.out.println(videoUrl.replaceAll(Pattern.quote("%"), "%25"));

		for (int i = 0; i < pages.size(); i++) {
			String page = pages.get(i);
			String accessToken = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getAccess_token();
			fdto.setAccess_token(accessToken);

			String pageId = getInstagramAccountDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getInstagram_business_account().getId();

			System.out.println(pageId);

			InstaDTO result = new RestTemplate()
					.postForEntity("https://graph.facebook.com/v20.0/" + pageId + "/media", fdto, InstaDTO.class)
					.getBody();

			System.out.println(result.getId());

			while (true) {

				String result2 = new RestTemplate().getForObject("https://graph.facebook.com/v20.0/" + result.getId()
						+ "?fields=status_code&access_token=" + accessToken, String.class);
				System.out.println(result2);

				if (result2.indexOf("FINISHED") != -1)
					break;
				else
					Thread.sleep(5000);

			}
			InstaCIDDTO cidDTO = new InstaCIDDTO();
			cidDTO.setCreation_id(result.getId());
			cidDTO.setAccess_token(accessToken);

			InstaDTO result1 = new RestTemplate().postForEntity(
					"https://graph.facebook.com/v20.0/" + pageId + "/media_publish", cidDTO, InstaDTO.class).getBody();

			System.out.println(result1.getId());

			AuditTrail audit = new AuditTrail();
			audit.setUser(userRepository.findByMobile(getUserName()).get());
			audit.setLine1("Posted to Instagram");
			audit.setLine2(accessToken);
			audit.setLine3(pageId);
			audit.setLine4(result + "");

			auditRepository.save(audit);
		}
		return "";
	}

	public String postToFacebookImage(String image, String template, List<String> pages) throws Exception {

		FacebookDTO fdto = new FacebookDTO();
		fdto.setMessage("This is Testing");

		String imageUrl = "";

		if (template.equals("Template 1"))
			imageUrl = getImageUrl(image, false, false);
		else
			imageUrl = getImageUrlTemplate2(image, false, false);

		fdto.setUrl(imageUrl);

		for (int i = 0; i < pages.size(); i++) {
			String page = pages.get(i);
			String accessToken = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getAccess_token();
			fdto.setAccess_token(accessToken);

			String pageId = getFacebookPageDetails().stream().filter(o -> o != null && o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getId();

			String result = new RestTemplate()
					.postForEntity("https://graph.facebook.com/v20.0/" + pageId + "/photos", fdto, String.class)
					.getBody();

			System.out.println(result);

			AuditTrail audit = new AuditTrail();
			audit.setUser(userRepository.findByMobile(getUserName()).get());
			audit.setLine1("Posted to Facebook");
			audit.setLine2(accessToken);
			audit.setLine3(pageId);
			audit.setLine4(result);

			auditRepository.save(audit);
		}
		return "";
	}

	public Boolean generateOTP(String mobile) throws Exception {

		HeidigiUser user = userRepository.findByMobile(Long.valueOf(mobile)).get();
		String emailId = user.getEmail();

		String password = generateOTP(4);

		user.setPassword(password);

		userRepository.save(user);

		return sendMessage(emailId, "Heidigi - OTP", "Your OTP is " + password);

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

	public LoginStatusDTO login(HeidigiLoginDTO login) {
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

	public LoginStatusDTO facebookLogin(String accessToken) throws Exception {

		try {

			String url = "https://graph.facebook.com/v20.0/me?access_token=" + accessToken
					+ "&debug=all&fields=id,name,email&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";

			FacebookPage page = restTemplate.getForObject(url, FacebookPage.class);

			String emailId = page.getEmail();

			System.out.println(page);

			HeidigiUser user = userRepository.findByEmail(emailId).get();

			HeidigiLoginDTO loginDTO = new HeidigiLoginDTO();
			loginDTO.setMobile(user.getMobile() + "");
			loginDTO.setPassword(user.getPassword());

			return login(loginDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error Occured while logging in " + ex);
			LoginStatusDTO loginStatus = new LoginStatusDTO();
			loginStatus.setLoginStatus(false);
			loginStatus.setMessage("Invalid Login....");
			return loginStatus;

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

	public Boolean sendMessage(String mailTo, String subject, String text) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(mailTo);
		msg.setSubject(subject);
		msg.setText(text);
		try {

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

	public String getCategory() throws Exception {

		return getRole().equals("Customer") ? userRepository.findByMobile(getUserName()).get().getCategory().getCname()
				: "";

//		return userRepository.findByMobile(getUserName()).get().getCategory().getCname();
	}

	public Boolean facebookToken() throws Exception {

		String token = profileRepository.findByMobile(getUserName()).get().getFacebookToken();

		return token != null && token.trim().length() != 0;

	}

	public Boolean saveFacebookToken(String accessToken) throws Exception {

		String url = "https://graph.facebook.com/v20.0/oauth/access_token?grant_type=fb_exchange_token&client_id="
				+ facebookAppId + "&client_secret=" + facebookSecret + "&fb_exchange_token=" + accessToken;
		Datum data = restTemplate.getForObject(url, Datum.class);

		System.out.println(data);

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();
		profile.setFacebookToken(data.getAccess_token());
		profileRepository.save(profile);

		return facebookToken();

	}

}
