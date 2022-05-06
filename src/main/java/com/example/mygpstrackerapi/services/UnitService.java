package com.example.mygpstrackerapi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Service
@Slf4j
public class UnitService {
	@Autowired
	private UserService userService;

	String token = "";

	@Value("${gpsgate.url}")
	private String gpsGateUrl;

	@Value("${gpsgate.username}")
	private String username;

	@Value("${gpsgate.password}")
	private String password;

	@Value("${gpsgate.unitRoleId}")
	private int unitRoleId;

	@Value("${gpsgate.applicationId}")
	private int applicationId;

	@PostConstruct
	public void init() {
		getAuthToken();
	}

	// Gets the users that are assigned to the unit role
	public ResponseEntity<JsonNode[]> getUnits() {
		if (token.equals("")) {
			getAuthToken();
		}

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
					JsonNode user = getUnitsById(usersIds.get(i).asInt()).getBody();
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
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/roles/" + roleId)
				.header("Authorization", token)
				.exchange()
				.block();

		if (clientResponse != null) {
			return clientResponse.toEntity(JsonNode.class).block();
		}
		return ResponseEntity.noContent().build();
	}

	public ResponseEntity<JsonNode[]> getUnitsByTag(String tag) {
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		JsonNode[] res = client.get()
				.uri("/applications/" + applicationId + "/tags/" + tag + "/users")
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(JsonNode[].class)
				.block();

		if (res != null && res.length > 0) {
			return ResponseEntity.ok(res);
		}
		return ResponseEntity.noContent().build();
	}

	// Gets the user with the given id
	public ResponseEntity<JsonNode> getUnitsById(int id) {
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		ClientResponse clientResponse = client.get()
				.uri("/applications/" + applicationId + "/users/" + id)
				.header("Authorization", token)
				.exchange()
				.block();

		if (clientResponse != null) {
			return clientResponse.toEntity(JsonNode.class).block();
		}
		return ResponseEntity.noContent().build();
	}

	private void getAuthToken() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();

		json.put("username", username);
		json.put("password", password);

		WebClient client = WebClient.create(gpsGateUrl);
		ObjectNode res = client.post()
				.uri("/applications/+" + applicationId + "/tokens")
				.body(Mono.just(json), ObjectNode.class)
				.retrieve()
				.bodyToMono(ObjectNode.class)
				.block();

		assert res != null;
		log.info(res.toString());

		token = res.get("token").asText();
	}
}
