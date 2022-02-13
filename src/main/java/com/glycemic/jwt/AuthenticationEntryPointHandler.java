package com.glycemic.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.glycemic.util.Error;
import com.glycemic.util.ErrorHandleType;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		Error error = new Error(HttpStatus.UNAUTHORIZED, ErrorHandleType.UNAUTHORIZED_ACCESS, "Authorization failed.", 
				authException.getLocalizedMessage(), 
				LocalDateTime.now().toLocalDate().toString(), 
				LocalDateTime.now().toLocalTime().toString());
		
		
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(error.toString());
	}

}
