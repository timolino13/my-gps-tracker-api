package com.example.mygpstrackerapi.services;

import com.example.mygpstrackerapi.components.GpsGateComponent;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Service
@Slf4j
public class UnitService {
	GpsGateComponent gpsGateComponent;

	@Value("${gpsgate.url}")
	private String gpsGateUrl;

	@Value("${gpsgate.unitRoleId}")
	private int unitRoleId;

	@Value("${gpsgate.applicationId}")
	private int applicationId;

	@Autowired
	public UnitService(GpsGateComponent gpsGateComponent) {
		this.gpsGateComponent = gpsGateComponent;
	}

	private String getToken() {
		return gpsGateComponent.getToken();
	}

	// Gets the users that are assigned to the unit role
	public ResponseEntity<JsonNode[]> getUnits() {
		ResponseEntity<JsonNode> res = getRoleById(unitRoleId);
		if (res.getStatusCode() == HttpStatus.OK) {
			JsonNode role = res.getBody();

			if (role == null) {
				return ResponseEntity.noContent().build();
			}
			JsonNode usersIds = role.get("usersIds");

			if (usersIds != null && usersIds.isArray()) {
				ArrayList<JsonNode> users = new ArrayList<>();
				for (int i = 0; i < usersIds.size(); i++) {
					JsonNode user = getUnitById(usersIds.get(i).asInt()).getBody();
					if (user != null) {
						users.add(user);
					}
				}
				if (users.size() > 0) {
					return ResponseEntity.ok(users.toArray(new JsonNode[0]));
				}
			}
		}

		return ResponseEntity.noContent().build();
	}

	// Gets a role containing the usersIds assigned to that role
	private ResponseEntity<JsonNode> getRoleById(int roleId) {
		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/roles/" + roleId)
				.header("Authorization", getToken())
				.exchange()
				.block();

		if (clientResponse != null) {
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(JsonNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}

	// Gets the user with the given id
	public ResponseEntity<JsonNode> getUnitById(int id) {
		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/users/" + id)
				.header("Authorization", getToken())
				.exchange()
				.block();

		if (clientResponse != null) {
			log.info("Headers: " + clientResponse.headers());
			return ResponseEntity.status(clientResponse.statusCode()).body(clientResponse.bodyToMono(JsonNode.class).block());
		}
		return ResponseEntity.noContent().build();
	}
}
