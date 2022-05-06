package com.example.mygpstrackerapi.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GpsGateComponent {

	private String token;

	@Value("${gpsgate.url}")
	private String gpsGateUrl;

	@Value("${gpsgate.applicationId}")
	private int applicationId;

	@Value("${gpsgate.username}")
	private String username;

	@Value("${gpsgate.password}")
	private String password;

	public String getToken() {
		if (token == null) {
			getAuthToken();
		}
		return token;
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
