package com.glycemic.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.service.CityService;
import com.glycemic.util.ResultTemplate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/city")
public class CityController {

	@Autowired
	private CityService cityService;
	
	@GetMapping(path="/list", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> getAllCity(){
		
		return ResponseEntity.ok(cityService.cityList());
	}
}
