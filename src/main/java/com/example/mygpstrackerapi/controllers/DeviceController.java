package com.example.mygpstrackerapi.controllers;

import com.example.mygpstrackerapi.services.DeviceService;
import com.example.mygpstrackerapi.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class DeviceController {

	private final UserService userService;

	private final DeviceService deviceService;

	@Autowired
	public DeviceController(UserService userService, DeviceService deviceService) {
		this.userService = userService;
		this.deviceService = deviceService;
	}

	@GetMapping("/{unitId}/devices")
	public ResponseEntity<ArrayNode> getDevicesByUnitId(@RequestHeader("Authorization") String token, @PathVariable int unitId) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return deviceService.getDevicesByUnitId(unitId);
		}
		return ResponseEntity.status(401).build();
	}

	@GetMapping("/{unitId}/devices/{deviceId}")
	public ResponseEntity<JsonNode> getDeviceByUnitIdAndDeviceId(@RequestHeader("Authorization") String token, @PathVariable int unitId, @PathVariable int deviceId) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return deviceService.getDeviceByUnitIdAndDeviceId(unitId, deviceId);
		}
		return ResponseEntity.status(401).build();
	}

	@PostMapping("/{unitId}/devices")
	public ResponseEntity<JsonNode> createDeviceForUnit(@RequestHeader("Authorization") String token, @PathVariable int unitId, @RequestBody JsonNode body) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return deviceService.createDeviceForUnit(unitId, body);
		}
		return ResponseEntity.status(401).build();
	}

	@DeleteMapping("/{unitId}/devices")
	public ResponseEntity<JsonNode> removeAllDevicesForUnit(@RequestHeader("Authorization") String token, @PathVariable int unitId) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return deviceService.removeAllDevicesFromUnit(unitId);
		}
		return ResponseEntity.status(401).build();
	}

	@DeleteMapping("/{unitId}/devices/{deviceId}")
	public ResponseEntity<JsonNode> removeDeviceFromUnit(@RequestHeader("Authorization") String token, @PathVariable int unitId, @PathVariable int deviceId) {
		String userId = userService.getUserIdByToken(token);
		if (userService.isVerified(userId) && userService.isAdmin(userId)) {
			return deviceService.removeDeviceFromUnit(unitId, deviceId);
		}
		return ResponseEntity.status(401).build();
	}
}
