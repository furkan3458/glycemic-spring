package com.glycemic.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/food")
public class FoodController {
	
	@Autowired
	private FoodService foodService;
	
	@GetMapping("/list")
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> list() {
        return ResponseEntity.ok(foodService.foodsList());
    }

    @GetMapping("/list/user")
    public ResponseEntity<LinkedHashMap<ResultTemplate,Object>> userFoodList() {
    	
    	return ResponseEntity.ok(foodService.userFoodList());
    }

    @DeleteMapping("/delete")
    public LinkedHashMap<ResultTemplate, Object> foodDelete(@RequestParam long gid) {
        return foodService.foodDelete(gid);
    }

    @PutMapping("/update")
    public LinkedHashMap<ResultTemplate,Object> foodUpdate(@RequestBody Food food){
        return foodService.update(food);
    }

    @PostMapping("/check")
    public Map<ResultTemplate, Object> foodCheck(@RequestParam long gid) {
    	return foodService.check(gid);
    }

    @JsonView(Views.ExceptList.class)
    @GetMapping(path="/foodGet", params= {"name"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public LinkedHashMap<ResultTemplate, Object> foodGet(@RequestParam String name) {
    	return foodService.getFood(name);
    }
}
