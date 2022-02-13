package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country,Long> {
	
	public Optional<Country> findCountryByName(String name);
	
}
