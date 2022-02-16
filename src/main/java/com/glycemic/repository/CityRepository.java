package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.City;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {
	
	public Optional<City> findCityByName(String name);
	
	public Optional<City> findCityByValue(String value);
	
}
