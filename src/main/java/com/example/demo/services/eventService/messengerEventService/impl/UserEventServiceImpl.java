package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.constcomponent.messengerEnums.Roles;
import com.example.demo.model.messanger.Messaging;
import com.example.demo.model.messanger.UserData;
import com.example.demo.services.eventService.messengerEventService.UserEventService;
import com.example.demo.services.messangerService.MessageParserService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.messangerService.PayloadParserService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.constcomponent.messengerEnums.Cases.CUSTOMER_REGISTER;
import static com.example.demo.constcomponent.messengerEnums.Cases.CUSTOMER_REGISTER_FINALIZE;
import static com.example.demo.constcomponent.messengerEnums.payloads.Payloads.GET_STARTED_PAYLOAD;
import static com.example.demo.constcomponent.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class UserEventServiceImpl implements UserEventService {
	@Autowired
	private MessageSenderService messageSenderService;
	@Autowired
	private RecognizeService recognizeService;
	@Autowired
	private UserRepositoryService userRepositoryService;

	@Autowired
	private MessageParserService messageParserService;
	@Autowired
	private PayloadParserService payloadParserService;


	@Override
	public void customerRegistration(Messaging messaging) {

		if (messaging.getPostback().getPayload().equals(GET_STARTED_PAYLOAD.name())) {
			userReg(messaging);
		} else {


			User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
			if (user.getStatus() == null) {
				messaging.getMessage().setText(CUSTOMER_REGISTER_FINALIZE.name());
				messageParserService.parseMessage(messaging);
			} else if (messaging.getMessage().getText().equals(CUSTOMER_REGISTER.name())) {
				messageSenderService.sendSimpleMessage(recognizeService.recognize(NAME_LASTNAME.name(), messaging.getSender().getId()), messaging.getSender().getId());
			} else {
				secondStep(messaging);
			}
		}
	}

	@Override
	public void changeStatus(Messaging messaging, String nextCommand) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		user.setStatus(nextCommand);
		userRepositoryService.saveAndFlush(user);
	}

	@Override
	public boolean isUser(User user) {
		return user.getAddress() == null || user.getPhoneNumber() == null;

	}

	private void userReg(Messaging messaging) {

		UserData userData = messageSenderService.sendFacebookRequest(messaging.getSender().getId());
		User user = new User();
		user.setRecipientId(messaging.getSender().getId());
		user.setName(userData.getFirstName());
		user.setLastName(userData.getLastName());
		user.setPicture(userData.getPicture());
		user.setRole(Roles.CUSTOMER);
		userRepositoryService.saveAndFlush(user);
		payloadParserService.parsePayload(messaging);
	}

	private void parseName(Messaging messaging) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());


		user.setName(messaging.getMessage().getText());
		user.setRole(Roles.CUSTOMER);
		userRepositoryService.saveAndFlush(user);
		messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());

	}

	private void secondStep(Messaging messaging) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());

		if (user.getName() == null) {
			parseName(messaging);
		} else if (user.getAddress() == null) {
			parseAddress(messaging, user);
		} else if (user.getEmail() == null) {
			parseEmail(messaging, user);
		} else if (user.getPhoneNumber() == null) {
			parsePhoneNumber(messaging, user);
		}
	}

	private void parseEmail(Messaging messaging, User user) {
		if (TextFormatter.isEmail(messaging)) {
			user.setEmail(messaging.getMessage().getText());
			userRepositoryService.saveAndFlush(user);
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}
	}

	private void parsePhoneNumber(Messaging messaging, User user) {
		if (TextFormatter.isPhoneNumber(messaging.getMessage().getText())) {
			user.setPhoneNumber(messaging.getMessage().getText());
			userRepositoryService.saveAndFlush(user);
			messageSenderService.sendSimpleMessage(recognizeService.recognize(CUSTOMER_ADDED_TO_DB.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(SUCCESS_REGISTER.name(), messaging.getSender().getId()), messaging.getSender().getId());
			user.setStatus(null);
			userRepositoryService.saveAndFlush(user);
		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}


	}

	private void parseAddress(Messaging messaging, User user) {
		if (TextFormatter.isCorrectAddress(messaging.getMessage().getText())) {
			user.setAddress(messaging.getMessage().getText());
			userRepositoryService.saveAndFlush(user);
			messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());

		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}

	}


}
