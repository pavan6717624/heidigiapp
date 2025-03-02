package com.heidigi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heidigi.model.JollyLoginDTO;
import com.heidigi.model.JollyLoginStatusDTO;
import com.heidigi.model.JollySignupDTO;
import com.heidigi.service.JollyServiceClass;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "JOLLY")
public class JollyController {
	
	@Autowired
	JollyServiceClass service;
	
	 public static final String ACCOUNT_SID = "AC00d36fe88cedc1d3681bce8b2ee878a2";
	    public static final String AUTH_TOKEN = "b0b98f4e6ab715fd4f543680bc32f7c9";
	
//	@RequestMapping(value = "whatsapp")
//	public String whatsapp() {
//
//	  Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//	  Conversation conversation = Conversation.creator().create();
//	  
//	  Participant participant = Participant.creator(conversation.getSid())
//              .setMessagingBindingAddress("whatsapp:+919449840144")
//              .setMessagingBindingProxyAddress("whatsapp:+918121908464")
//              .create();
//     
//      return "";
//	}
//	
//
//	
//	@RequestMapping(value = "whatsapprespond")
//	public String whatsapprespond(String MessagingServiceSid, String EventType, String Attributes, String DateCreated, String Index, String ChatServiceSid,
//			String MessageSid, String AccountSid, String Source, String RetryCount,String Author, String ParticipantSid, String Body, String ConversationSid ) {
//
//		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//		
//		System.out.println(ConversationSid+" "+MessageSid);
//		
//		 Message message =
//		            Message.creator(ConversationSid).setAuthor("Pavan").setBody("Ahoy there!").create();
//
//		
//	
//	  System.out.println(message.getSid());
//     
//      return "";
//	}

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


}
