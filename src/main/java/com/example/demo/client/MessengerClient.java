package com.example.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessengerClient extends com.messanger.client.MessengerClient {

	public MessengerClient(@Value("${page.access.token}") String accessToken) {
		super(accessToken);
	}

	@Override
	public void errorMessage(Long recipient) {

	}

	@Override
	public void sendSimpleQuestion(Long recipient, String text, String payload, String splitter) {

	}
}
