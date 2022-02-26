package com.glycemic.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.glycemic.jwt.JwtUtils;
import com.glycemic.model.City;
import com.glycemic.model.JwtSession;
import com.glycemic.model.Roles;
import com.glycemic.model.UserActivation;
import com.glycemic.model.UserResetPassword;
import com.glycemic.model.Users;
import com.glycemic.repository.CityRepository;
import com.glycemic.repository.JwtSessionRepository;
import com.glycemic.repository.RoleRepository;
import com.glycemic.repository.UserActivationRepository;
import com.glycemic.repository.UserRepository;
import com.glycemic.repository.UserResetPasswordRepository;
import com.glycemic.request.ActivationRequest;
import com.glycemic.request.LoginRequest;
import com.glycemic.request.ResetPasswordRequest;
import com.glycemic.response.LoginResponse;
import com.glycemic.security.UserDetailsImpl;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ERole;
import com.glycemic.util.EStatus;
import com.glycemic.util.ResultTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private JwtSessionRepository jwtRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private CityRepository cityRepo;
	
	@Autowired
	private UserActivationRepository activationRepo;
	
	@Autowired
	private UserResetPasswordRepository resetPasswordRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private MailService mailService;
	
	@Value("${app.activationExpire}")
	private Long activationExpireTime;
	
	@Value("${app.resetPassExpire}")
	private Long resetExpireTime;
	
	public LinkedHashMap<ResultTemplate,Object> login(LoginRequest loginRequest, String userAgent, String remoteAddr, String fingerPrint){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Authentication is not reachable.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		
		if(userAgent == null || remoteAddr == null || fingerPrint == null) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Missing headers.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		}
		else if(!loginRequest.getEmail().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email is invalid.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
			
		}
		else if(!loginRequest.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Password must be at least 8 characters long and contain uppercase letters, numbers and special characters.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		}
		else if (!userRepo.existsByEmail(loginRequest.getEmail())) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email not found!");
			result.put(EResultInfo.errors, HttpStatus.UNAUTHORIZED);
		}	
		else {
			try {
				Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);

				String jwt = jwtUtils.generateJwtToken(authentication,loginRequest.getRememberMe());

				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
				
				JwtSession jwtSession = new JwtSession();
				
				Users user = new Users(userDetails.getId(), 
						userDetails.getEmail(),
						userDetails.getPassword(),
						userDetails.getName(),
						userDetails.getSurname(),
						userDetails.getFullname(),
						userDetails.getEnable(),
						userDetails.getCity());
				
				List<JwtSession> sessionList = jwtRepo.findAllByUsers(user);
				
				
				if(!sessionList.isEmpty()) {
					//User logged already.
					//Detect before session, if match current user-agent then delete and return unauth
					
					for(JwtSession session : sessionList) {
						if(session.getFingerPrint() != null && session.getFingerPrint().equals(fingerPrint)) {
							log.info("Double login attempt detected. User:{user:"+loginRequest.getEmail()+", session:"+session.getId()+"}");
							jwtRepo.delete(session);
							result.put(EResultInfo.status, false);
							result.put(EResultInfo.message, "Error: Authentication already.");
							result.put(EResultInfo.errors, HttpStatus.UNAUTHORIZED);
							return result;
						}
					}
					
				}
				
				jwtSession.setUsers(user);
				jwtSession.setJwttoken(jwt);
				jwtSession.setExpiretime(loginRequest.getRememberMe() ? null : jwtUtils.getExpireTimeFromJwtToken(jwt));
				jwtSession.setRemoteAddr(remoteAddr);
				jwtSession.setUserAgent(userAgent);
				jwtSession.setFingerPrint(fingerPrint);
				
				jwtRepo.save(jwtSession);
				
				LoginResponse response = new LoginResponse(
						userDetails.getId(),
						jwt,
						userDetails.getEmail(),
						userDetails.getFullname(),
						userDetails.getName(),
						userDetails.getSurname(),
						userDetails.getCreatedBy(),
						userDetails.getModifiedBy(),
						userDetails.getCreatedDate(),
						userDetails.getModifiedDate(),
						userDetails.getEnable()
						);
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.message, "Giriş başarılı.");
				result.put(EResultInfo.result, response);
			}
			catch(AuthenticationException exception) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Authentication failed.");
				result.put(EResultInfo.errors, HttpStatus.UNAUTHORIZED);
			}
			
		}
		
		return result;
	}

	@Transactional
	public LinkedHashMap<ResultTemplate,Object> register(Users signup, String userAgent, String remoteAddr, String fingerPrint) throws MessagingException{
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Authentication is not reachable.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		
		if(userAgent == null || remoteAddr == null || fingerPrint == null) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Missing headers.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		}	
		else if(!signup.getEmail().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email is invalid.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
			
		}
		else if(!signup.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Password must be at least 8 characters long and contain uppercase letters, numbers and special characters.");
			result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		}
		else if (userRepo.existsByEmail(signup.getEmail())) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email is already in use!");
			result.put(EResultInfo.errors, HttpStatus.UNAUTHORIZED);
		}
		else{
			// Create new user's account
			Users user = new Users(
					signup.getEmail(),
					encoder.encode(signup.getPassword()),
					signup.getName(),
					signup.getSurname(),
					false
				);

			List<Roles> roles = new ArrayList<>();
			Roles userRole = roleRepo.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			
			City city = cityRepo.findById(signup.getCity().getId()).orElseThrow(() -> new RuntimeException("Error: City is not found."));
			
			roles.add(userRole);
			user.setEnable(false);
			user.setRoles(roles);
			user.setCity(city);
			user.setFullname(user.getName()+" "+user.getSurname());
			user.setCreatedBy(user.getEmail());
			user.setModifiedBy(user.getEmail());
			user = userRepo.save(user);
			
			try {
				Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(signup.getEmail(), signup.getPassword()));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				String jwt = jwtUtils.generateJwtToken(authentication,false);
				
				mailService.welcome(user.getEmail(), user.getName());

				jwtRepo.save(new JwtSession(user,jwt,jwtUtils.getExpireTimeFromJwtToken(jwt), remoteAddr, userAgent, fingerPrint));
				
				LoginResponse response = new LoginResponse(
						user.getId(),
						jwt,
						user.getEmail(),
						user.getFullname(),
						user.getName(),
						user.getSurname(),
						user.getCreatedBy(),
						user.getModifiedBy(),
						user.getCreatedDate(),
						user.getModifiedDate(),
						user.getEnable()
						);
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.message, "Kayıt başarılı.");
				result.put(EResultInfo.result, response);
			}
			catch(AuthenticationException exception) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Authentication failed.");
				result.put(EResultInfo.errors, HttpStatus.UNAUTHORIZED);
			}	
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> activate(ActivationRequest request){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Activation is invalid.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		
		Optional<UserActivation> activationOpt = activationRepo.findByUserEmailAndUuid(request.getEmail(), request.getActivateKey());
		
		if(activationOpt.isPresent()) {
			UserActivation activation = activationOpt.get();
			if(activation.getActivated()) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Already activated.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.ALREADY.ordinal());
			}
			else if(activation.getCreatedDate() + activationExpireTime < System.currentTimeMillis()) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Activation is expired.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.EXPIRED.ordinal());
			}
			else {
				activation.setActivated(true);
				activationRepo.save(activation);
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.message, "Activation success.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.OK.ordinal());
			}
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> validateReset(String email, String forgetKey){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Parameters are invalid.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		result.put(EResultInfo.result, EStatus.INVALID.ordinal());
		
		Optional<UserResetPassword> resetOpt = resetPasswordRepo.findByUserEmailAndUuid(email, forgetKey);
		
		if(resetOpt.isPresent()) {
			UserResetPassword reset = resetOpt.get();			
			
			if(reset.getUsed()) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Link already used.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.ALREADY.ordinal());
			}
			else if(reset.getCreatedDate() + resetExpireTime < System.currentTimeMillis()) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Password reset is expired.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.EXPIRED.ordinal());
			}
			else {
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.message, "Link validated.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.OK.ordinal());
			}	
		}
		
		return result;
	}
	
	@Transactional
	public LinkedHashMap<ResultTemplate,Object> resetPassword(ResetPasswordRequest resetReq){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.message, "Error: Parameters are invalid.");
		result.put(EResultInfo.errors, HttpStatus.BAD_REQUEST);
		result.put(EResultInfo.result, EStatus.INVALID.ordinal());
		
		Optional<UserResetPassword> resetOpt = resetPasswordRepo.findByUserEmailAndUuid(resetReq.getEmail(), resetReq.getForgetKey());
		
		if(resetOpt.isPresent()) {
			
			if(!resetReq.getPassword().equals(resetReq.getPasswordConfirm())) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Parameters are invalid.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.EXPIRED.ordinal());
			}
			else if(!resetReq.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Error: Password must be at least 8 characters long and contain uppercase letters, numbers and special characters.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.ALREADY.ordinal());
			}
			else {
				UserResetPassword reset = resetOpt.get();
				Users user = reset.getUser();
				
				user.setPassword(encoder.encode(resetReq.getPassword()));
				reset.setUsed(true);
				
				resetPasswordRepo.save(reset);
				userRepo.save(user);
				
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.message, "Parola başarıyla değiştirildi.");
				result.put(EResultInfo.errors, HttpStatus.OK);
				result.put(EResultInfo.result, EStatus.OK.ordinal());
			}
		}
		
		return result;
	}
	
}
