package com.glycemic.service;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.glycemic.mail.EmailServiceImpl;
import com.glycemic.model.UserActivation;
import com.glycemic.model.Users;
import com.glycemic.repository.UserActivationRepository;
import com.glycemic.repository.UserRepository;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.Generator;
import com.glycemic.util.ResultTemplate;

@Service
public class MailService {

	@Autowired
	private EmailServiceImpl emailServiceImpl;
	
	@Autowired
	private UserActivationRepository activationRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Value("${app.siteUrl}")
	private String siteUrl;
	
	private static final String RECIPIENT_NAME = "recipientName";
	private static final String SENDER = "senderName";
	private static final String ACTIVATE_URL = "activateUrl";
	
	@Transactional
	public String createActivateUrl(String email) {
		String uuid = Generator.generateUUID();
		String url = "";
		
		Optional<UserActivation> activationOpt = activationRepo.findByUserEmail(email);
		
		if(activationOpt.isPresent()) {
			activationRepo.delete(activationOpt.get());
		}
		
		Optional<Users> userOpt = userRepo.findByEmail(email);
		
		if(userOpt.isPresent()) {
			UserActivation activation = new UserActivation();
			activation.setId(0L);
			activation.setUser(userOpt.get());
			activation.setUuid(uuid);
			activation.setActivated(false);
			activationRepo.save(activation);
			
			url = siteUrl+"activation?activateKey="+uuid+"&to="+email;
		}
		
		return url;
	}
	
	public LinkedHashMap<ResultTemplate,Object> welcome(String to, String name) throws MessagingException{
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		LinkedHashMap<String,Object> templateModel = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Authentication is not reachable.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		
		String activateUrl = createActivateUrl(to);
		
		if(!activateUrl.isBlank() && !activateUrl.isEmpty()) {
			templateModel.put(RECIPIENT_NAME, name);
			templateModel.put(SENDER, "GlycemicApp");
			templateModel.put(ACTIVATE_URL, activateUrl);
			
			emailServiceImpl.sendSimpleMessageWithTemplate(to, "Hoşgeldiniz", templateModel);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.message, "Mesaj Gönderildi.");
			result.put(EResultInfo.errors, HttpStatus.OK);
		}
		
		return result;
	}
}
