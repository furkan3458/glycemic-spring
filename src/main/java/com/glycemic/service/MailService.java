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
import com.glycemic.model.UserResetPassword;
import com.glycemic.model.Users;
import com.glycemic.repository.UserActivationRepository;
import com.glycemic.repository.UserRepository;
import com.glycemic.repository.UserResetPasswordRepository;
import com.glycemic.util.EMailStatus;
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
	private UserResetPasswordRepository resetPasswordRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Value("${app.siteUrl}")
	private String siteUrl;
	
	private static final String RECIPIENT_NAME = "recipientName";
	private static final String SENDER = "senderName";
	private static final String ACTIVATE_URL = "activateUrl";
	private static final String FORGET_PASS_URL = "forgetPasswordUrl";
	
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
	
	@Transactional
	public String createForgetPasswordUrl(String email) {
		String uuid = Generator.generateUUID();
		String url = "";
		
		Optional<UserResetPassword> activationOpt = resetPasswordRepo.findByUserEmailAndUsed(email, false);
		
		if(activationOpt.isPresent()) {
			resetPasswordRepo.delete(activationOpt.get());
		}
		
		Optional<Users> userOpt = userRepo.findByEmail(email);
		
		if(userOpt.isPresent()) {
			UserResetPassword resetPassword = new UserResetPassword();
			resetPassword.setId(0L);
			resetPassword.setUser(userOpt.get());
			resetPassword.setUuid(uuid);
			resetPassword.setUsed(false);
			resetPasswordRepo.save(resetPassword);
			
			url = siteUrl+"reset?forgetKey="+uuid+"&to="+email;
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
			
			emailServiceImpl.sendSimpleMessageWithTemplate(to, "Hoşgeldiniz", "mail-welcome.html", templateModel);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.message, "Mesaj Gönderildi.");
			result.put(EResultInfo.errors, HttpStatus.OK);
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> forgetPassword(String to){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		LinkedHashMap<String,Object> templateModel = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Some mistakes");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		result.put(EResultInfo.result, EMailStatus.ERROR.ordinal());
		
		
		
		Optional<Users> user = userRepo.findByEmail(to);
		
		if(user.isEmpty()) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Mail adresi geçersiz.");
			result.put(EResultInfo.errors, HttpStatus.OK);
			result.put(EResultInfo.result, EMailStatus.INVALID.ordinal());
		}
		else {
			String forgetPasswordUrl = createForgetPasswordUrl(to);
			
			if(!forgetPasswordUrl.isBlank() && !forgetPasswordUrl.isEmpty()) {
				templateModel.put(SENDER, "GlycemicApp");
				templateModel.put(FORGET_PASS_URL, forgetPasswordUrl);
				
				try {
					emailServiceImpl.sendSimpleMessageWithTemplate(to, "Şifreni sıfırla","mail-forgetPassword.html", templateModel);
					
					result.put(EResultInfo.status, true);
					result.put(EResultInfo.message, "Mesaj Gönderildi.");
					result.put(EResultInfo.errors, HttpStatus.OK);
					result.put(EResultInfo.result, EMailStatus.SUCCESS.ordinal());
				}
				catch(MessagingException e) {
					result.put(EResultInfo.status, false);
					result.put(EResultInfo.message, "Mail gönderirken bir hata ile karşılaşıldı.");
					result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
					result.put(EResultInfo.result, EMailStatus.ERROR.ordinal());
				}
			}
		}
		
		return result;
	}
}
