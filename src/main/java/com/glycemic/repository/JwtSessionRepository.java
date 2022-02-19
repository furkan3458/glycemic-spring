package com.glycemic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glycemic.model.JwtSession;
import com.glycemic.model.Users;

@Repository
public interface JwtSessionRepository extends JpaRepository<JwtSession,Long>{
	
	public Optional<JwtSession> findByUsers(Users users);
	
	public List<JwtSession> findAllByUsers(Users users);
	
	public Optional<JwtSession> findByJwttoken(String jwttoken);
}
