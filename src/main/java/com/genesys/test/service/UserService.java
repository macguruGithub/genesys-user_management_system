package com.genesys.test.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.genesys.test.dto.UserData;
import com.genesys.test.entity.User;
import com.genesys.test.exception.BaseException;
import com.genesys.test.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	public List<User> getUsers(){
		List<User> userList = userRepo.findAll();
		if(userList.size() > 0) {
			return userList;
		}else {
			return new ArrayList<User>();
		}
	}
	
	public User getUser(String username) {
		Optional<User> user = userRepo.findByUsername(username);
		
		
		if(user.isPresent()) {
			return user.get();
		}else {
			return null;
		}
		
	}
	
	public User createOrUpdateUser(UserData userData) {
		
		Optional<User> userContainer = userRepo.findByUsername(userData.getUsername());
		User user = null;
		if(userContainer.isPresent())
			user = userContainer.get();
		else {
			user = new User();
		}
			user.setUsername(userData.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(userData.getPassword()));
			user.setEmail(userData.getEmail());
			Date date  = new Date();
			userData.setLastUpdateDate(date);
			user.setLastLoginDate(userData.getLastUpdateDate());
			userRepo.save(user);
			return user;
		
	}
	
	public Boolean deleteUser(String username) {
		Optional<User> userContainer = userRepo.findByUsername(username);
		if(userContainer.isPresent()) {
			userRepo.delete(userContainer.get());
			return true;
		}else {
			return false;
		}
	}
	
	public Boolean login(UserData userData) {
		 Optional<User> user = userRepo.findByUsername(userData.getUsername());
         if (user.isPresent()) {
             String encodedPassword = user.get().getPassword();
             if(bCryptPasswordEncoder.matches(userData.getPassword(), encodedPassword)) {
            	 return true;
             }else {
            	 BaseException exp = new BaseException("Er_002","Login","Invalid Password");
            	 exp.setHttpStatus(HttpStatus.UNAUTHORIZED);
            	 throw exp;
             }
		} else {
			BaseException exp = new BaseException("Er_002", "Login", "User Not found");
			exp.setHttpStatus(HttpStatus.NOT_FOUND);
			throw exp;
		}
		
		
	}
}
