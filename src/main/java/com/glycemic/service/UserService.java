package com.glycemic.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.glycemic.repository.UserRepository;
import com.glycemic.request.LoginRequest;
import com.glycemic.util.ResultTemplate;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	public LinkedHashMap<ResultTemplate,Object> login(LoginRequest login){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		return result;
	}

}
