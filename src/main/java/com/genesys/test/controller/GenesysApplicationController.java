package com.genesys.test.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genesys.test.dto.UserData;
import com.genesys.test.entity.User;
import com.genesys.test.service.UserService;

@RestController
@RequestMapping("/")
public class GenesysApplicationController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/get-all-user", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<?>> getUserList() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@GetMapping(value = "/get-user", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUser(@RequestParam(value = "username", required = true) String username) {
		User user = userService.getUser(username);
		if (user == null) {
			return (ResponseEntity<?>) ResponseEntity.notFound();
		} else {
			return ResponseEntity.ok(user);
		}
	}

	@SuppressWarnings("serial")
	@PostMapping(value = "/update-or-create", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateOrCreate(@RequestBody UserData userdata) {
		Character[] specialChars = new Character[] { '!', '#', '$', '%', '^', '&', '*', '(', ')', '-', '/', '~', '[',
				']', '@' };
		List<Character> badCharacter = Arrays.asList(specialChars);
		String email = userdata.getEmail();
		String name = email.substring(0, email.indexOf('@'));
		String domain = email.substring(email.indexOf('@') + 1, email.length());
		if (isInvalidEmail(name, badCharacter) || isInvalidEmail(domain, badCharacter)) {
			return new ResponseEntity<>(new LinkedHashMap<String, String>() {
				{
					put("status", "failed");
					put("errorMessage", "invalid email");
				}
			}, HttpStatus.BAD_REQUEST);
		}

		if ( checkPassword(true, true, true, 8, 10, userdata.getPassword())) {
			return new ResponseEntity<>(new LinkedHashMap<String, String>() {
				{
					put("status", "failed");
					put("errorMessage", "password does not meet the requirements");
				}
			}, HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(userService.createOrUpdateUser(userdata));
	}

	@SuppressWarnings("serial")
	@DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deleteUser(@RequestParam(value = "username", required = true) String username) {
		return userService.deleteUser(username) ? ResponseEntity.ok(new LinkedHashMap<String, String>() {
			{
				put("status", "success");
			}
		}) : new ResponseEntity<>(new LinkedHashMap<String, String>() {
			{
				put("status", "success");
			}
		}, HttpStatus.NOT_FOUND);
	}

	public static Boolean isInvalidEmail(String part, List<Character> bad) {
		if (bad.contains(part.charAt(0)))
			return true;
		if (bad.contains(part.charAt(part.length() - 1)))
			return true;
		return false;
	}

	public static boolean checkPassword(boolean forceSpecialChar, boolean forceCapitalLetter, boolean forceNumber,
			int minLength, int maxLength, String password) {
		String pattern = null;
		StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");

		if (forceSpecialChar) {
			patternBuilder.append("(?=.*[@#$%])");
		}

		if (forceCapitalLetter) {
			patternBuilder.append("(?=.*[A-Z])");
		}

		if (forceNumber) {
			patternBuilder.append("(?=.*d)");
		}

		patternBuilder.append(".{" + minLength + "," + maxLength + "})");
		pattern = patternBuilder.toString();

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(password);
		return m.matches();
	}

}
