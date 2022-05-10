package com.example.mygpstrackerapi.services;

import com.example.mygpstrackerapi.components.GpsGateComponent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DeviceService {
	GpsGateComponent gpsGateComponent;

	UnitService unitService;

	@Value("${gpsgate.url}")
	private String gpsGateUrl;

	@Value("${gpsgate.applicationId}")
	private int applicationId;

	@Autowired
	public DeviceService(GpsGateComponent gpsGateComponent, UnitService unitService) {
		this.gpsGateComponent = gpsGateComponent;
		this.unitService = unitService;
	}

	private String getToken() {
		return gpsGateComponent.getToken();
	}

	public ResponseEntity<ArrayNode> getDevicesByUnitId(int unitId) {
		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/users/" + unitId + "/devices")
				.header("Authorization", getToken())
				.exchange()
				.block();

		if (clientResponse != null) {
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(ArrayNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}

	public ResponseEntity<JsonNode> getDeviceByUnitIdAndDeviceId(int unitId, int deviceId) {
		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/users/" + unitId + "/devices/" + deviceId)
				.header("Authorization", getToken())
				.exchange()
				.block();

		if (clientResponse != null) {
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(JsonNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}

	public ResponseEntity<JsonNode> createDeviceForUnit(int unitId, JsonNode device) {
		removeAllDevicesFromUnit(unitId);

		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.post()
				.uri("/applications/" + applicationId + "/users/" + unitId + "/devices")
				.header("Authorization", getToken())
				.body(Mono.just(device), JsonNode.class)
				.exchange()
				.block();

		if (clientResponse != null) {
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(JsonNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<JsonNode> removeAllDevicesFromUnit(int unitId) {
		ArrayNode devices = getDevicesByUnitId(unitId).getBody();
		if (devices == null || devices.size() == 0) {
			return ResponseEntity.badRequest().body(new TextNode("Unit does not have a device"));
		}
		for (JsonNode device : devices) {
			removeDeviceFromUnit(unitId, device.get("id").asInt());
		}
		return ResponseEntity.ok(new TextNode("Removed all devices from unit"));
	}

	public ResponseEntity<JsonNode> removeDeviceFromUnit(int unitId, int deviceId) {
		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.delete()
				.uri("/applications/" + applicationId + "/users/" + unitId + "/devices/" + deviceId)
				.header("Authorization", getToken())
				.exchange()
				.block();

		if (clientResponse != null) {
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(JsonNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}
}
