package com.example.demo.services.messangerService.impl;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.model.messanger.Entry;
import com.example.demo.model.messanger.Event;
import com.example.demo.model.messanger.Messaging;
import com.example.demo.services.messangerService.*;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventParserServiceImpl implements EventParserService {
	private static final Logger logger = Logger.getLogger(EventParserServiceImpl.class);
	@Autowired
	UserRepositoryService userRepositoryService;
	@Autowired
	private MessageSenderService messageSenderService;
	@Autowired
	private QuickReplyParserService quickReplyParserService;
	@Autowired
	private MessageParserService messageParserService;
	@Autowired
	private PayloadParserService payloadParserService;

	@Override
	public boolean parseEvent(Event event) {
		for (Entry e : event.getEntry()) {


			for (Messaging messaging : e.getMessaging()) {
				try {
					if (messaging.getPostback() != null) {

						payloadParserService.parsePayload(messaging);
						return true;
					} else if (messaging.getMessage() != null) {
						messageParserService.parseMessage(messaging);

						return true;


					}
				} catch (Exception ex) {
					User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
					if (user.getStatus() != null) {
						user.setStatus(null);
						userRepositoryService.saveAndFlush(user);
					}
					ex.printStackTrace();
					logger.warn(ex);
					messageSenderService.errorMessage(messaging.getSender().getId());
					return true;
				}
			}
		}
		return false;

	}
}
