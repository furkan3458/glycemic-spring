package com.glycemic.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glycemic.model.Nutritional;
import com.glycemic.service.NutritionalService;
import com.glycemic.util.ResultTemplate;
import com.glycemic.validator.NutritionalAllValidator;
import com.glycemic.validator.NutritionalValidator;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/nutritional")
public class NutritionalController {
	
	@Autowired
	private NutritionalService nutritionalService;
	
	@GetMapping(path="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> list() {
        return ResponseEntity.ok(nutritionalService.nutritionalList());
    }

	@PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
	@PutMapping(path="/insert", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> insertCategory(@RequestBody @Validated(value = NutritionalValidator.class) Nutritional category){
		
		return ResponseEntity.ok(nutritionalService.insert(category));
	}
	
	@PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
	@PostMapping(path="/update", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> updateCategory(@RequestBody @Validated(value = NutritionalAllValidator.class) Nutritional category){
		
		return ResponseEntity.ok(nutritionalService.update(category));
	}
	
	@PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
	@DeleteMapping(path="/delete", params={"id"}, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> deleteCategory(@RequestParam("id") Long id){
		
		return ResponseEntity.ok(nutritionalService.delete(id));
	}
}
