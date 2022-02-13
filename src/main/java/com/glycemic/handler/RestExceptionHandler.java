package com.glycemic.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.glycemic.util.Error;
import com.glycemic.util.ErrorHandleType;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        
		String message = ex.getAllErrors().get(0).getDefaultMessage();
		
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.ARGUMENT_NOT_VALID, message, ex.getObjectName() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
		
		Error error = new Error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorHandleType.MEDIA_TYPE_NOT_SUPPORTED, "Only supported content type is application/json", ex.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
		
		Error error = new Error(HttpStatus.METHOD_NOT_ALLOWED, ErrorHandleType.METHOD_NOT_SUPPORTED, "Use GET,POST,DELETE,PUT methods.", ex.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
		
		Error error = new Error(HttpStatus.BAD_REQUEST, ErrorHandleType.MESSAGE_NOT_READABLE, "Change your parameters.", ex.getLocalizedMessage() ,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
