package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.UserActivation;

@Repository
public interface UserActivationRepository extends JpaRepository<UserActivation,Long>{
	
	public Optional<UserActivation> findByUserEmail(String email);
	
	public Optional<UserActivation> findByUserEmailAndUuid(String email, String uuid);
}
