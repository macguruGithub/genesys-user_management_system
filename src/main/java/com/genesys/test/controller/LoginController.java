package com.genesys.test.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genesys.test.dto.UserData;
import com.genesys.test.service.UserService;

@RestController
@RequestMapping("/")
public class LoginController {
	
	 
	
	@Autowired
	private UserService userService;
	
	@SuppressWarnings("serial")
	@PostMapping(value="/login", produces=MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> login(@RequestBody UserData userData){
		userService.login(userData);
		return ResponseEntity.ok(new LinkedHashMap<String,String>() {{put("status", "success");}});
	}

}
