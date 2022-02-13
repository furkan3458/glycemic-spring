package com.glycemic.handler;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
	@ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
	public ResponseEntity<Error> sqlIntegrityConstraintViolationHandler(SQLIntegrityConstraintViolationException exception){
		String errorMessage = "Check your values. You entered duplicated value on unique column.";
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.DATA_INTEGRITY_CONSTRAINT_VIOLATION , errorMessage, exception.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return ResponseEntity.badRequest().body(error);
	}
	
	@ExceptionHandler(value = {SQLException.class})
	public ResponseEntity<Error> sqlIntegrityConstraintViolationHandler(SQLException exception){
		String errorMessage = "You entered wrong or invalid parameters values.";
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.SQL_EXCEPTION , errorMessage, exception.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return ResponseEntity.badRequest().body(error);
	}
}
