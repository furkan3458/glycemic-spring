package com.glycemic.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.service.SearchService;
import com.glycemic.util.ResultTemplate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	SearchService searchService;

	@GetMapping(path="/search", params={"q","category","page"}, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> searchFoodParameter(@RequestParam String q, @RequestParam String category, @RequestParam int page){
		
		return ResponseEntity.ok(searchService.searchWith(q,category,page));
	}
}
