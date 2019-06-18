package com.example.demo.client;

import com.messanger.Messaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessengerClient extends com.messanger.client.MessengerClient {

	public MessengerClient(@Value("${page.access.token}") String accessToken,
						   @Value("${server.url}") String server, @Value("${url.map.urlMap}") String urlMap) {
		super(accessToken, server, "/WebHook", urlMap);
	}

	@Override
	public void errorMessage(Messaging messaging) {

	}
}
