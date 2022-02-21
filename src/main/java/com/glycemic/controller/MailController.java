package com.glycemic.controller;

import java.util.LinkedHashMap;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.service.MailService;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ResultTemplate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/mail")
public class MailController {
	
	@Autowired
	private MailService mailService;

	@GetMapping(path="/test")
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> sendTestMail() throws MessagingException{
		
		return ResponseEntity.ok(mailService.welcome("furkanuguz3458@gmail.com","Furkan"));
	}
	
	@PostMapping(path="/forget", params={"email"}, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> forgetMail(@RequestParam("email") String email) throws MessagingException {
		LinkedHashMap<ResultTemplate,Object> body = mailService.forgetPassword(email);
		
		HttpStatus status = (HttpStatus)body.get(EResultInfo.errors);
		return new ResponseEntity<>(body,status);
	}
}
