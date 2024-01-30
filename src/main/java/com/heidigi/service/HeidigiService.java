package com.heidigi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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
import com.heidigi.model.Datum;
import com.heidigi.model.FacebookDTO;
import com.heidigi.model.FacebookPage;
import com.heidigi.model.HeidigiSignupDTO;
import com.heidigi.model.ImageDTO;
import com.heidigi.model.LoginStatusDTO;
import com.heidigi.model.ProfileDTO;
import com.heidigi.repository.AuditRepository;
import com.heidigi.repository.HeidigiImageRepository;
import com.heidigi.repository.HeidigiProfileRepository;
import com.heidigi.repository.HeidigiRoleRepository;
import com.heidigi.repository.HeidigiUserRepository;
import com.heidigi.repository.HeidigiVideoRepository;

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
	HeidigiImageRepository heidigiImageRepository;

	@Autowired
	AuditRepository auditRepository;

	@Autowired
	HeidigiVideoRepository heidigiVideoRepository;

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

	public LoginStatusDTO signup(HeidigiSignupDTO signup) {

		LoginStatusDTO loginStatus = new LoginStatusDTO();

		Optional<HeidigiUser> userOpt = userRepository.findByMobile(Long.valueOf(signup.getMobile()));
		;

		if (!userOpt.isPresent()) {
			HeidigiUser user = new HeidigiUser();
			user.setEmail(signup.getEmail());
			user.setMobile(Long.valueOf(signup.getMobile()));
			user.setName(signup.getName());
			user.setPassword(signup.getPassword());
			user.setMessage("Customer Signup");
			user.setRole(
					roleRepository.findByRoleName(signup.getRole().equals("Business") ? "Customer" : "Designer").get());

			userRepository.save(user);
			loginStatus.setLoginStatus(
					userRepository.findByMobileAndPassword(user.getMobile(), user.getPassword()).isPresent());
			loginStatus.setMessage("Login Successful");
		} else {
			loginStatus.setLoginStatus(false);
			loginStatus.setMessage("Mobile number already Exists...");
		}

		return loginStatus;

	}

	public String uploadImage(MultipartFile file, String category, String subCategory) throws Exception {
		HeidigiImage image = uploadImage(file, category, subCategory, "Image");
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

		// System.out.println("Backup Ended
		// "+downloadVideo(uploadResult1.get("public_id")+" ")+new Date());

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

	public HeidigiImage uploadImage(MultipartFile file, String category, String subCategory, String type)
			throws Exception {

		File convFile = new File(UUID.randomUUID() + "" + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		InputStream is = new ByteArrayInputStream(file.getBytes());
		BufferedImage img = ImageIO.read(is);

		if (!type.equals("Logo"))
			img = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, 300, 300);

		String extension = file.getOriginalFilename().split("\\.")[file.getOriginalFilename().split("\\.").length - 1]
				.toLowerCase();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (extension.toLowerCase().equals("jpg"))
			ImageIO.write(img, "JPEG", bos);
		else if (extension.toLowerCase().equals("png"))
			ImageIO.write(img, "PNG", bos);

		String imageText = new String(Base64.encodeBase64(bos.toByteArray()), "UTF-8");

		System.out.println(imageText);

		Map uploadResult1 = cloudinary1.uploader().upload(convFile, ObjectUtils.emptyMap());

		HeidigiImage image = new HeidigiImage();
		image.setCategory(category);
		image.setSubcategory(subCategory);
		image.setType(type);
		image.setPublicId(uploadResult1.get("public_id") + "");

		image.setResponse(uploadResult1.toString());

		image.setExtension(extension);
		image.setUser(userRepository.findByMobile(getUserName()).get());
		image.setImageText(imageText);

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

		profile.setLogo(uploadImage(file, "Logo", "Logo", "Logo"));
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

		profile.setPhoto(uploadImage(file, "Photo", "Photo", "Photo"));
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

		List<HeidigiImage> images = heidigiImageRepository.getImageIds(getUserName(), getRole());
		return images.stream().map(o -> new ImageDTO(o)).collect(Collectors.toList());
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
		ProfileDTO profileDTO = new ProfileDTO();

		if (profileOpt.isPresent()) {
			HeidigiProfile profile = profileOpt.get();

			profileDTO.setAddress(profile.getAddress());
//		profileDTO.setImage(getImage(profile.getLogo().get, profile.getLogo().getExtension()));
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
			profileDTO.setTemplate(profile.getTemplate() == null ? "Template 1" : profile.getTemplate());
		} else {

			profileDTO.setLogo("hu8doewfg7syktb1xo8l");
			profileDTO.setPhoto("hu8doewfg7syktb1xo8l");
			profileDTO.setTemplate("Template 1");
		}
		return profileDTO;
	}

//	public String getTemplate(String template) {
//
//		return heidigiImageRepository.getTemplateImages().stream().limit(1).map(o -> {
//			try {
//
//				return template(o, template);
//
//			} catch (Exception e) {
//				return "";
//			}
//		}).collect(Collectors.toList()).get(0);
//	}

//	public String template(String image, String template) throws Exception {
//		if (template == null || template.trim().length() == 0)
//			template = "Template 1";
//		if (template.equals("Template 1"))
//			return getImageUrl(image, true);
//		else
//			return getImageUrlTemplate2(image, true);
//	}

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
				.opacity(100).radius(30).width(0.15).x(10).y(10).crop("scale").chain();

		if (watermark)
			transformation = transformation.overlay(new Layer().publicId("mvj11zgltg9mqjgy7z4d")).chain()
					.flags("layer_apply", "relative").gravity("north_west").opacity(20).radius(30).width(1080)
					.height(1080).x(0).y(0).crop("scale").chain();

		transformation = transformation.overlay(new Layer().publicId("akdvbdniqfbncjrapghb")).chain()
				.flags("layer_apply", "relative").gravity("south_west").width(0.65).height(0.18).opacity(100).chain()

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
				.opacity(100).radius(30).width(0.15).x(10).y(10).crop("scale").chain();

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
			imageUrl = URLDecoder.decode(getImageUrl(image, false,false), "UTF-8");
		else
			imageUrl = URLDecoder.decode(getImageUrlTemplate2(image, false, false), "UTF-8");

		System.out.println("Download :: " + imageUrl);
		String imageStr = getImage(imageUrl, false);
		return imageStr;

	}
	
	public String showTemplate(String image, String template) throws Exception {

		String imageUrl = "";

		if (template.equals("Template 1"))
			imageUrl = URLDecoder.decode(getImageUrl(image, false,true), "UTF-8");
		else
			imageUrl = URLDecoder.decode(getImageUrlTemplate2(image, false, true), "UTF-8");

		System.out.println("Download :: " + imageUrl);
		String imageStr = getImage(imageUrl, false);
		return imageStr;

	}

	public String getImage(String id, String ext) {

		try {

			String url = "https://res.cloudinary.com/hwlyozehf/image/upload/" + id + ".jpg";
			byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

			String image = new String(Base64.encodeBase64(imageBytes), "UTF-8");

			return "data:image/" + ext + ";base64," + image;
		} catch (Exception ex) {
			return "";
		}

	}

	public String getImage(String url, Boolean template) {

		try {

			byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

//
//			if (template)
//			{
//				InputStream is = new ByteArrayInputStream(imageBytes);
//				BufferedImage img = ImageIO.read(is);
//				img = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, 300, 300);
//			}

			String image = new String(Base64.encodeBase64(imageBytes), "UTF-8");

			return "{\"img\":\"" + "data:image/jpeg;base64," + image + "\"}";

		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
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

	public String downloadVideo(String publicId, String template) throws Exception {
		if (template.equals("Template 1"))
			return downloadVideo(publicId);
		else
			return downloadVideoTemplate2(publicId);

	}

	public String downloadVideo(String publicId) throws Exception {

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

		String videoUrl = cloudinary1.url().transformation(transformation.height(1080).width(1080).crop("scale").chain()

				// logo
				.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative").gravity("north_west")
				.opacity(100).radius(30).width(0.15).x(10).y(10).crop("scale").chain()

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

	public String downloadVideoTemplate2(String video) throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String logoId = profile.getLogo().getPublicId();

		String address = profile.getAddress();
		String website = profile.getWebsite();

		System.out.println(logoId);

		Transformation transformation = new Transformation();

		String videoUrl = cloudinary1.url().transformation(transformation.height(1080).width(1080).crop("scale").chain()

				// logo
				.overlay(new Layer().publicId(logoId)).chain().flags("layer_apply", "relative").gravity("north_east")
				.opacity(100).radius(30).width(0.15).x(10).y(10).crop("scale").chain()

				// 100% bottom background
				.overlay(new Layer().publicId("v6s3p850kn4aozfltfjd")).chain().flags("layer_apply", "relative")
				.gravity("south").width(1).height(0.04).y(30).opacity(100).chain()

				.overlay(new TextLayer().fontFamily("montserrat").fontSize(25).textAlign("center")
						.text("☎ 9449 840 144 | ☸ " + website + " | ⚲ " + address))
				.flags("layer_apply", "relative").gravity("south").y(35).color("white").chain()

		).videoTag(video + ".mp4");

		List<String> urls = Arrays.asList(videoUrl.split("<source src='"));

		return urls.stream().filter(o -> o.indexOf(".mp4") != -1).map(o -> o.substring(0, o.indexOf(".mp4") + 4))
				.collect(Collectors.toList()).get(0);

	}

	public String postToFacebookVideo(String video) throws Exception {
		FacebookDTO fdto = new FacebookDTO();
		fdto.setAccess_token(
				"EAAEEWuiBKkIBOZB25ips1OnzE8dk52A5iQIZA3TdfZCw4f8gdu0po7fjeX25mq8OtcBwh3Qm55ZBquDGqzA9zJqvPMJY8aQaxO9dudQ4hVJLHPnJY1LjVt58uZBoXiUf0rZATnWteJtLwgIW2zklpfEY3eoYp4FSZCblC1ZB6Lolumktm96rrEAKBzaY7ZAMu");

//		fdto.setAccess_token(
//				"EAAarYZB0lCY8BO8n4IQdDutQF04KhRjc1R9lcXgk7cPFUrDcNmmEG77kZBZCseFSkCBQCreAtyFGkPWoAidLX40urPFXNzGwh0b0L7SHjfZAN5nqlz3v6NYyEGXNiIDVhxAYTXE2GZAx0tp0EvRZCXSumagDOEKjZCLaygSN0tJRPJXNyXdNHq6BzR1Lhg6ZAZBZAkNjClgVIBxYUZBiVRwm84eL0JZA0JUpMEy9vlFxrZCoZD");
//		
		fdto.setMessage("This is Testing");

		String template = getProfile().getTemplate();

		String videoUrl = downloadVideo(video);
//		if (template.equals("Template 1"))
//			imageUrl = getImageUrl(image, false);
//		else
//			imageUrl = getImageUrlTemplate2(image, false);

//		fdto.setFile_url(video);

		System.out.println("Facebook :: " + videoUrl + "\n" + fdto);

		fdto.setFile_url(videoUrl);

		String result = new RestTemplate()
				.postForEntity("https://graph-video.facebook.com/v18.0/145448711978153/videos", fdto, String.class)
				.getBody();
//			.postForEntity("https://graph-video.facebook.com/v18.0/178235032042634/videos", fdto, String.class) 
//				.getBody();

		System.out.println(result);

		return "";
	}

	public List<Datum> getFacebookPageDetails() throws Exception {

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();

		String accessToken = profile.getFacebookToken();

		String url = "https://graph.facebook.com/v18.0/me?access_token=" + accessToken
				+ "&debug=all&fields=id,name,accounts&format=json&method=get&pretty=0&suppress_http_code=1&transport=cors";
		FacebookPage fpage = restTemplate.getForObject(url, FacebookPage.class);

		return fpage.getAccounts().getData();

	}

	public List<String> getFacebookPageNames() throws Exception {
		return getFacebookPageDetails().stream().map(o -> o.getName()).sorted().collect(Collectors.toList());
	}

	public String postToFacebookImage(String image, String template, List<String> pages) throws Exception {

		FacebookDTO fdto = new FacebookDTO();
		fdto.setMessage("This is Testing");

		String imageUrl = "";

		if (template.equals("Template 1"))
			imageUrl = getImageUrl(image, false,false);
		else
			imageUrl = getImageUrlTemplate2(image, false, false);

		fdto.setUrl(imageUrl);

		for (int i = 0; i < pages.size(); i++) {
			String page = pages.get(i);
			String accessToken = getFacebookPageDetails().stream().filter(o -> o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getAccess_token();
			fdto.setAccess_token(accessToken);

			String pageId = getFacebookPageDetails().stream().filter(o -> o.getName().equals(page))
					.collect(Collectors.toList()).get(0).getId();

			String result = new RestTemplate()
					.postForEntity("https://graph.facebook.com/v18.0/" + pageId + "/photos", fdto, String.class)
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

	public Boolean facebookToken() throws Exception {

		String token = profileRepository.findByMobile(getUserName()).get().getFacebookToken();

		return token != null && token.trim().length() != 0;

	}

	public Boolean saveFacebookToken(String accessToken) throws Exception {

		String url = "https://graph.facebook.com/v18.0/oauth/access_token?grant_type=fb_exchange_token&client_id="
				+ facebookAppId + "&client_secret=" + facebookSecret + "&fb_exchange_token=" + accessToken;
		Datum data = restTemplate.getForObject(url, Datum.class);

		System.out.println(data);

		HeidigiProfile profile = profileRepository.findByMobile(getUserName()).get();
		profile.setFacebookToken(data.getAccess_token());
		profileRepository.save(profile);

		return facebookToken();

	}

	public ProfileDTO changeTemplate(String template) throws Exception {

		Optional<HeidigiProfile> profileOpt = profileRepository.findByMobile(getUserName());
		HeidigiProfile profile = null;

		if (!profileOpt.isPresent()) {
			profile = new HeidigiProfile();
			profile.setUser(userRepository.findByMobile(getUserName()).get());
		} else
			profile = profileOpt.get();

		profile.setTemplate(template);

		profileRepository.save(profile);

		return getProfile();

	}

}
