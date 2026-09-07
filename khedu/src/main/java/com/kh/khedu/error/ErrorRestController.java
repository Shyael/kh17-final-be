package com.kh.khedu.error;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@RestControllerAdvice(annotations = {RestController.class})
@RestControllerAdvice(basePackages = {"com.kh.khedu.controller"})
public class ErrorRestController {
	
	@ExceptionHandler(TargetNotfoundException.class)
	public ResponseEntity<String> notFound() {
//		return ResponseEntity.notFound().build();
		return ResponseEntity.status(404).body("Target not found");
	}
	
	@ExceptionHandler(value = {		
			WhoAreYouException.class,
			JwtValidationException.class
	})
	public ResponseEntity<String> whoAreYou() {
		return ResponseEntity.status(401).body("not authorized");
	}
	
	@ExceptionHandler(value = {	GetOutException.class })
	public ResponseEntity<String> getOut() {
		return ResponseEntity.status(403).body("need permission");
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<String> badRequest(Exception e) {
		log.debug("e = {}", e.getMessage());
		return ResponseEntity.status(400).body("requirement mismatch");
	}
	
	@ExceptionHandler(value = {YouAreNotAdminException.class})
	public ResponseEntity<String> areNotAdmin(){
		return ResponseEntity.status(403).body("you are not admin");
	}
}
