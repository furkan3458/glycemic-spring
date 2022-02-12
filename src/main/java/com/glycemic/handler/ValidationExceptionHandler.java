package com.glycemic.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.glycemic.util.Error;
import com.glycemic.util.ErrorHandleType;

@ControllerAdvice
public class ValidationExceptionHandler {
	@ExceptionHandler(value = {ConstraintViolationException.class})
	public ResponseEntity<Error> constraintViolationHandler(ConstraintViolationException exception){
		String errorMessage = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.CONSTRAINT_VIOLATION , errorMessage, exception.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return ResponseEntity.badRequest().body(error);
	}
}
