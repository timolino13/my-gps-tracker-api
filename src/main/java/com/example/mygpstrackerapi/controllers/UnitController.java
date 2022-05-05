package com.example.mygpstrackerapi.controllers;

import com.example.mygpstrackerapi.services.UnitService;
import com.example.mygpstrackerapi.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
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
	public JsonNode[] getUnits(@RequestHeader("Authorization") String token) {
		if (userService.isAdmin(userService.getUserIdByToken(token))) {
			return unitService.getUnits();
		}
		return null;
	}

	@GetMapping("/{id}")
	public JsonNode getUnitById(@PathVariable String id, @RequestHeader("Authorization") String token) {
		if (userService.isAdmin(userService.getUserIdByToken(token))) {
			return unitService.getUnitsById(id);
		}
		return null;
	}
}
