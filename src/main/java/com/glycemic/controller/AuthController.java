package com.glycemic.controller;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.jwt.JwtUtils;
import com.glycemic.model.JwtSession;
import com.glycemic.model.Users;
import com.glycemic.repository.JwtSessionRepository;
import com.glycemic.repository.UserRepository;
import com.glycemic.request.ActivationRequest;
import com.glycemic.request.LoginRequest;
import com.glycemic.request.ResetPasswordRequest;
import com.glycemic.request.ValidateRequest;
import com.glycemic.response.ValidateResponse;
import com.glycemic.service.AuthService;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ResultTemplate;
import com.glycemic.validator.UserValidator;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService	authService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private JwtSessionRepository jwtRepo;

	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping(path="/login", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
		
		String userAgent = request.getHeader("User-Agent");
		String fingerPrint = request.getHeader("Fingerprint");
		String remoteAddr = request.getRemoteAddr();
		
		LinkedHashMap<ResultTemplate,Object> body = authService.login(loginRequest, userAgent, remoteAddr, fingerPrint);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		
		return new ResponseEntity<>(body,status);
	}
	
	@PostMapping(path="/signup", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> signup(@RequestBody @Validated(value = UserValidator.class) Users signup, HttpServletRequest request) throws MessagingException{
		String userAgent = request.getHeader("User-Agent");
		String fingerPrint = request.getHeader("Fingerprint");
		String remoteAddr = request.getRemoteAddr();
		
		LinkedHashMap<ResultTemplate,Object> body = authService.register(signup, userAgent, remoteAddr, fingerPrint);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		
		return new ResponseEntity<>(body,status);
	}
	
	@PostMapping(path="/activate", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> activateUser(@RequestBody ActivationRequest activationRequest) {
		LinkedHashMap<ResultTemplate,Object> body = authService.activate(activationRequest);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		return new ResponseEntity<>(body,status);
	}
	
	@GetMapping(path="/validate_reset", params={"forgetKey","email"}, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> validateReset(@RequestParam("forgetKey") String forgetKey, @RequestParam("email") String email) {
		LinkedHashMap<ResultTemplate,Object> body = authService.validateReset(email, forgetKey);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		return new ResponseEntity<>(body,status);
	}
	
	@PostMapping(path="/reset_password", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
		LinkedHashMap<ResultTemplate,Object> body = authService.resetPassword(request);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		return new ResponseEntity<>(body,status);
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
