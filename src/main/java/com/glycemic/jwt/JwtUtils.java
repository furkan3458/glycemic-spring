package com.glycemic.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.glycemic.security.UserDetailsImpl;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication, boolean isRememberMe) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		JwtBuilder jwtBuilder = Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS512, jwtSecret);
		
		if(!isRememberMe) 
			jwtBuilder.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs));
		
		return jwtBuilder.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public Long getExpireTimeFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration().getTime();
	}

	public void validateToken(String authToken) throws JwtException{
		Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
	}
}