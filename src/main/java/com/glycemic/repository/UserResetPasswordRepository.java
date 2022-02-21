package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glycemic.model.UserActivation;
import com.glycemic.model.UserResetPassword;

public interface UserResetPasswordRepository extends JpaRepository<UserResetPassword,Long>{

	public Optional<UserActivation> findByUserEmail(String email);
	
	public Optional<UserActivation> findByUserEmailAndUsed(String email, Boolean used);
	
	public Optional<UserActivation> findByUserEmailAndUuid(String email, String uuid);
}
