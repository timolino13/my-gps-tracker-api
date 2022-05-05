package com.example.mygpstrackerapi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

	public JsonNode[] getUnits() {
		if (token.equals("")) {
			getAuthToken();
		}

		JsonNode role = getRoleById(unitRoleId);

		assert role != null;
		JsonNode usersIds = role.get("usersIds");

		if (usersIds != null && usersIds.isArray()) {
			ArrayList<JsonNode> users = new ArrayList<>();
			for (int i = 0; i < usersIds.size(); i++) {
				users.add(getUnitsById(usersIds.get(i).asText()));
			}

			return users.toArray(new JsonNode[0]);
		}

		return null;
	}

	private JsonNode getRoleById(int roleId) {
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		JsonNode res = client.get()
				.uri("/applications/" + applicationId + "/roles/" + roleId)
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		assert res != null;
		return res;
	}

	public JsonNode[] getUnitsByTag(String tag) {
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		return client.get()
				.uri("/applications/" + applicationId + "/tags/" + tag + "/users")
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(JsonNode[].class)
				.block();
	}

	public JsonNode getUnitsById(String id) {
		if (token.equals("")) {
			getAuthToken();
		}

		WebClient client = WebClient.create(gpsGateUrl);

		return client.get()
				.uri("/applications/" + applicationId + "/users/" + id)
				.header("Authorization", token)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
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
