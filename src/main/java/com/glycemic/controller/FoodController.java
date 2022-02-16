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

import com.fasterxml.jackson.annotation.JsonView;
import com.glycemic.model.Food;
import com.glycemic.service.FoodService;
import com.glycemic.util.ResultTemplate;
import com.glycemic.validator.FoodAllValidator;
import com.glycemic.validator.FoodValidator;
import com.glycemic.view.NutritionalView;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/food")
public class FoodController {
	
	@Autowired
	private FoodService foodService;
	
	@GetMapping(path="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> list() {
        return ResponseEntity.ok(foodService.foodList());
    }

    @GetMapping(path="/list/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> userFoodList() {
    	return ResponseEntity.ok(foodService.userFoodList());
    }

    @PreAuthorize("hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    @DeleteMapping(path="/delete", params= {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodDelete(@RequestParam("id") Long id) {
    	return ResponseEntity.ok(foodService.delete(id));
    }

    @PutMapping(path="/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodInsert(@RequestBody @Validated(value = FoodValidator.class) Food food){
    	return ResponseEntity.ok(foodService.insert(food));
    }
    
    @PostMapping(path="/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodUpdate(@RequestBody @Validated(value = FoodAllValidator.class) Food food){
    	return ResponseEntity.ok(foodService.update(food));
    }

    @PostMapping(path="/check", params= {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodCheck(@RequestParam("id") Long id) {
    	return ResponseEntity.ok(foodService.check(id));
    }

    @JsonView(NutritionalView.ExceptFood.class)
    @GetMapping(path="/get", params= {"name"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodGetByName(@RequestParam("name") String name) {
    	return ResponseEntity.ok(foodService.getByName(name, "reject"));
    }
    
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('SUPER_ADMIN')")
    @JsonView(NutritionalView.ExceptFood.class)
    @GetMapping(path="/get", params= {"name","status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> foodGetByNameAndStatus(@RequestParam("name") String name, @RequestParam("status") String status) {
    	return ResponseEntity.ok(foodService.getByName(name, status));
    }
}
