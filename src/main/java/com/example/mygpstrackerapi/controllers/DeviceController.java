package com.example.mygpstrackerapi.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

	@GetMapping()
	public String getDevices() {

		return "Devices";
	}

	@GetMapping("/{id}")
	public String getDeviceById(@PathVariable String id) {
		return "Device with id: " + id;
	}

	@GetMapping("/tag/{tag}")
	public String getDevicesByTag(@PathVariable String tag) {
		return "Devices with tag: " + tag;
	}
}
