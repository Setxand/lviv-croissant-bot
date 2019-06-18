package com.example.demo;

import com.example.demo.client.MessengerClient;
import com.example.demo.client.TelegramClientEx;
import com.example.demo.constcomponent.Platform;
import com.example.demo.model.messanger.Shell;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import telegram.Chat;
import telegram.Message;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class ServerStarting {
	private static final Logger logger = Logger.getLogger(ServerStarting.class);
	@Autowired TelegramClientEx clientChB;
	@Autowired MessengerClient messengerClient;
	//	@Value("${page.access.token}")
//	private String PAGE_ACCESS_TOKEN;
//	@Value("${profile.api.uri}")
//	private String FACEBOOK_PROFILE_URI;
//	@Value("${server.url}")
//	private String SERVER_URL;
//	@Value("${telegram.urlmap}") String URL_MAP;
	@Value("${server.url}") private String SERVER_URL;

	@PostConstruct
	public void getStarted() throws Exception {
		Shell shell = new Shell();
		shell.setWhiteListedDomains(new ArrayList<>(Arrays.asList(SERVER_URL)));

		try {

		} catch (HttpClientErrorException ex) {
			logger.warn("Messenger queries error: ", ex);
		} catch (Exception ex) {
			logger.warn("Error:", ex);
		} finally {
			try {
				clientChB.setWebHooks();
				Message message = new Message(new Chat(388073901));
				message.setPlatform(Platform.COMMON_BOT);
				clientChB.simpleMessage("Server has ran", message);
			} catch (Exception e) {
				logger.warn(e);
			}
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		messengerClient.setWebHooks();
	}


}


