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

import com.glycemic.model.Category;
import com.glycemic.service.CategoryService;
import com.glycemic.util.ResultTemplate;
import com.glycemic.validator.CategoryAllValidator;
import com.glycemic.validator.CategoryValidator;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping(path="/list", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> getAllCategory(){
		
		return ResponseEntity.ok(categoryService.categoryList());
	}
	
	@GetMapping(path="/foods", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> getAllCategoryWithFoods(){
		
		return ResponseEntity.ok(categoryService.categoryListWithFoods());
	}

	@GetMapping(path="/find", params={"name"},  produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> getOneByNameUrl(@RequestParam("name") String name){
		
		return ResponseEntity.ok(categoryService.getCategoryByName(name));
	}
	
	@GetMapping(path="/find", params={"id"},  produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> getOneById(@RequestParam("id") Long id){
		
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping(path="/insert", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> insertCategory(@RequestBody @Validated(value = CategoryValidator.class) Category category){
		
		return ResponseEntity.ok(categoryService.insert(category));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(path="/update", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> updateCategory(@RequestBody @Validated(value = CategoryAllValidator.class) Category category){
		
		return ResponseEntity.ok(categoryService.update(category));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(path="/delete", params={"id"}, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> deleteCategory(@RequestParam("id") Long id){
		
		return ResponseEntity.ok(categoryService.delete(id));
	}
}
