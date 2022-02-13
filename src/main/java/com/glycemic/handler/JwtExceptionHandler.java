package com.glycemic.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.glycemic.util.Error;
import com.glycemic.util.ErrorHandleType;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtExceptionHandler {

	public void jwtException(HttpServletRequest request, HttpServletResponse response, JwtException exception){
		String errorMessage = "Invalid json web token.";
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.JWT_SIGNATURE, errorMessage, exception.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());

		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(error.toString());
		} catch (IOException e) {
			log.error("Could not responded because the writer was not found or missing.");
		}
	}
}
