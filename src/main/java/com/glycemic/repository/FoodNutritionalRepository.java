package com.glycemic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.Food;
import com.glycemic.model.FoodNutritional;

@Repository
public interface FoodNutritionalRepository extends JpaRepository<FoodNutritional, Long>{

	public List<FoodNutritional> findAllByFood(Food food);
	
	public List<FoodNutritional> findAllByFoodId(Integer foodId);
}
