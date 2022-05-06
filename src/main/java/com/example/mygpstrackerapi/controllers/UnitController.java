package com.example.mygpstrackerapi.controllers;

import com.example.mygpstrackerapi.services.UnitService;
import com.example.mygpstrackerapi.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

	private final UnitService unitService;

	private final UserService userService;

	@Autowired
	public UnitController(UnitService unitService, UserService userService) {
		this.unitService = unitService;
		this.userService = userService;
	}

	@GetMapping()
	public ResponseEntity<JsonNode[]> getUnits(@RequestHeader("Authorization") String token) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return unitService.getUnits();
		}
		return ResponseEntity.status(401).build();
	}

	@GetMapping("/{unitId}")
	public ResponseEntity<JsonNode> getUnitById(@RequestHeader("Authorization") String token, @PathVariable int unitId) {
		if (userService.isVerified(userService.getUserIdByToken(token))) {
			return unitService.getUnitById(unitId);
		}
		return ResponseEntity.status(401).build();
	}
}
