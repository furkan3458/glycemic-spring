package com.glycemic.controller;

import java.util.LinkedHashMap;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.service.MailService;
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
}
