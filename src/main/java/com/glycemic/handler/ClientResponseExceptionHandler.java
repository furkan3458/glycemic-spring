package com.glycemic.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.glycemic.util.Error;
import com.glycemic.util.ErrorHandleType;

@ControllerAdvice
public class ClientResponseExceptionHandler {

	@ExceptionHandler(value = {AccessDeniedException.class})
	public ResponseEntity<Error> accessDeniedHandler(AccessDeniedException exception){
		String errorMessage = "Your role access is not acceptable for this trasaction.";
		Error error = new Error(HttpStatus.FORBIDDEN, ErrorHandleType.ACCESS_DENIED, errorMessage, exception.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return ResponseEntity.badRequest().body(error);
	}
}
