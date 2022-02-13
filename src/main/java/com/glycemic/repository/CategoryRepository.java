package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>{

	public Optional<Category>  findByUrlEqualsIgnoreCase(String url);
	
	public Optional<Category>  findByNameEqualsIgnoreCase(String name);
}
