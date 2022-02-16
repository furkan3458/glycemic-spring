package com.glycemic.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.jwt.JwtUtils;
import com.glycemic.model.City;
import com.glycemic.model.JwtSession;
import com.glycemic.model.Roles;
import com.glycemic.model.Users;
import com.glycemic.repository.CityRepository;
import com.glycemic.repository.JwtSessionRepository;
import com.glycemic.repository.RoleRepository;
import com.glycemic.repository.UserRepository;
import com.glycemic.request.LoginRequest;
import com.glycemic.request.ValidateRequest;
import com.glycemic.response.LoginResponse;
import com.glycemic.response.ValidateResponse;
import com.glycemic.security.UserDetailsImpl;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ERole;
import com.glycemic.util.ResultTemplate;
import com.glycemic.validator.UserValidator;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private JwtSessionRepository jwtRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private CityRepository countryRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping(path="/login", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> login(@RequestBody LoginRequest loginRequest){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
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
		Optional <JwtSession> jwtSessionOptional = jwtRepo.findByUsers(user);
		
		if(jwtSessionOptional.isEmpty()) 
			jwtSession.setUsers(user);
		else 
			jwtSession = jwtSessionOptional.get();
		
		jwtSession.setJwttoken(jwt);
		jwtSession.setExpiretime(loginRequest.getRememberMe() ? null : jwtUtils.getExpireTimeFromJwtToken(jwt));	
		
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
		result.put(EResultInfo.errors, 0);
		result.put(EResultInfo.message, "Giriş başarılı.");
		result.put(EResultInfo.result, response);
			
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(path="/signup", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> signup(@RequestBody @Validated(value = UserValidator.class) Users signup){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		if(!signup.getEmail().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email is invalid.");
			result.put(EResultInfo.errors, 1);
			
		}
		else if(!signup.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Password must be at least 8 characters long and contain uppercase letters, numbers and special characters.");
			result.put(EResultInfo.errors, 2);
		}
		else if (userRepo.existsByEmail(signup.getEmail())) {
			result.put(EResultInfo.status, false);
			result.put(EResultInfo.message, "Error: Email is already in use!");
			result.put(EResultInfo.errors, 2);
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
			
			City country = countryRepo.findById(signup.getCity().getId()).orElseThrow(() -> new RuntimeException("Error: City is not found."));
			
			roles.add(userRole);
			user.setEnable(false);
			user.setRoles(roles);
			user.setCity(country);
			user.setFullname(user.getName()+" "+user.getSurname());
			user.setCreatedBy(user.getEmail());
			user.setModifiedBy(user.getEmail());
			user = userRepo.save(user);
			
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(signup.getEmail(), signup.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication,false);

			jwtRepo.save(new JwtSession(user,jwt,jwtUtils.getExpireTimeFromJwtToken(jwt)));
			
//			new JwtResponse(jwt, 
//					 user.getId(), 
//					 user.getFullname(),
//					 user.getUsername(), 
//					 user.getEmail(), 
//					 listRoles,
//					 false)
			
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
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kayıt başarılı.");
			result.put(EResultInfo.result, response);
		}

		return ResponseEntity.ok(result);
	}
	
	@PostMapping(path="/validate", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateUser(@RequestBody ValidateRequest validateRequest) {
		
		Optional<JwtSession> jwtSessionOptional;
		
		if(validateRequest.getToken().isEmpty() || validateRequest.getToken() == null || validateRequest.getToken().isBlank() ||
			validateRequest.getEmail().isEmpty() || validateRequest.getEmail() == null || validateRequest.getEmail().isBlank()) {
			return ResponseEntity.ok(new ValidateResponse(false, 1, "Cannot acceptable."));
		}
		else if(!jwtUtils.getValidateToken(validateRequest.getToken()) || !jwtUtils.getEmailFromJwtToken(validateRequest.getToken()).equals(validateRequest.getEmail())) {
			return ResponseEntity.ok(new ValidateResponse(false, 2, "Cannot acceptable."));
		}
		else if((jwtSessionOptional = jwtRepo.findByJwttoken(validateRequest.getToken())).isEmpty() || !jwtSessionOptional.get().getUsers().getEmail().equals(validateRequest.getEmail())) {
			return ResponseEntity.ok(new ValidateResponse(false, 3, "Cannot acceptable."));
		}
			
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
	
	@PostMapping(path="/validate_email", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateEmail(@RequestBody ValidateRequest validateRequest) {
		
		Optional<Users> user = userRepo.findByEmail(validateRequest.getEmail());
		
		if(user.isPresent()) {
			return ResponseEntity.ok(new ValidateResponse(false, 0, "User found with that email."));
		}
		
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
	
	@PostMapping(path="/logout", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> logout(@RequestBody ValidateRequest validateRequest) {
		
		Optional<JwtSession> session = jwtRepo.findByJwttoken(validateRequest.getToken());
		
		if(session.isEmpty()) {
			
			return ResponseEntity.ok(new ValidateResponse(false, 0, "Token not found. Session is invalid but logout can applicable."));
		}
		
		jwtRepo.delete(session.get());
		
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
}
