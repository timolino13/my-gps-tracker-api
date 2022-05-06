package com.example.mygpstrackerapi.controllers;

import com.example.mygpstrackerapi.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

	private final UserService userService;

	@Autowired
	public DeviceController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<JsonNode[]> getDevicesByUnitId(@RequestHeader("Authorization") String token, @PathVariable String id) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return null;
		}
		return ResponseEntity.status(401).build();
	}

	@PostMapping("/{id}")
	public ResponseEntity<JsonNode> createDeviceForUnit(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody JsonNode body) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return null;
		}
		return ResponseEntity.status(401).build();
	}
}
