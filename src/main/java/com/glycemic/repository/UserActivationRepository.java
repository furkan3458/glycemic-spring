package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glycemic.model.UserActivation;

public interface UserActivationRepository extends JpaRepository<UserActivation,Long>{
	
	public Optional<UserActivation> findByUserEmail(String email);
}
