package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glycemic.model.Nutritional;

public interface NutritionalRepository extends JpaRepository<Nutritional,Long>{
	
	public Optional<Nutritional> findByName(String name);

}
