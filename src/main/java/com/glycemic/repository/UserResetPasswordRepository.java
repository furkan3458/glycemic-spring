package com.glycemic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glycemic.model.UserActivation;
import com.glycemic.model.UserResetPassword;

public interface UserResetPasswordRepository extends JpaRepository<UserResetPassword,Long>{

	public Optional<UserResetPassword> findByUserEmail(String email);
	
	public Optional<UserResetPassword> findByUserEmailAndUsed(String email, Boolean used);
	
	public Optional<UserResetPassword> findByUserEmailAndUuid(String email, String uuid);
}
