package com.genesys.test.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class CustomExceptionHandler {
	
	@ExceptionHandler({BaseException.class, MethodArgumentNotValidException.class})
	public ResponseEntity<Object> handleBusinessException(WebRequest request, Exception ex) {
		
		Map<String,String> response = new LinkedHashMap<>();
		BaseException e = (BaseException) ex;
		if(e instanceof BaseException) {
			response.put("errorCode", e.getErrorCode());
			response.put("errorModule", e.getErrorModule());
			response.put("errorMessage", e.getErrorMessage());
		}
		
		response.put("status", "falied");
		
		
		System.out.println(e.getErrorCode()+" "+e.getErrorModule()+" "+e.getErrorMessage());
		
		return new ResponseEntity<>(response, e.getHttpStatus());
	}
}
