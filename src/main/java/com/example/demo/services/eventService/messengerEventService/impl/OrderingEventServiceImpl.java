package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entity.SupportEntity;
import com.example.demo.entity.lvivCroissants.Croissant;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.User;
import com.example.demo.model.messanger.Messaging;
import com.example.demo.services.eventService.messengerEventService.OrderingEventService;
import com.example.demo.services.eventService.messengerEventService.UserEventService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.repositoryService.SupportEntityRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.constcomponent.messengerEnums.Cases.*;
import static com.example.demo.constcomponent.messengerEnums.payloads.QuickReplyPayloads.ACCEPT_ORDERING_PAYLOAD;
import static com.example.demo.constcomponent.messengerEnums.payloads.QuickReplyPayloads.ADDRESS_PAYLOAD;
import static com.example.demo.constcomponent.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class OrderingEventServiceImpl implements OrderingEventService {
	@Autowired
	private CroissantRepositoryService croissantRepositoryService;
	@Autowired
	private UserRepositoryService userRepositoryService;
	@Autowired
	private MessageSenderService messageSenderService;
	@Autowired
	private CustomerOrderingRepositoryService customerOrderingRepositoryService;
	@Autowired
	private RecognizeService recognizeService;
	@Autowired
	private UserEventService userEventService;
	@Autowired
	private SupportEntityRepositoryService supportEntityRepositoryService;

	@Override
	public void parseOrdering(Messaging messaging) {
		if (messaging.getPostback() != null)
			orderingCreator(messaging);
		else
			orderingFinalist(messaging);
	}


	private void orderingCreator(Messaging messaging) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		String croissantId = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
		Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(croissantId));

		if (supportEntityRepositoryService.getByUserId(messaging.getSender().getId()) != null) {
			if (supportEntityRepositoryService.getByUserId(messaging.getSender().getId()).getOneMore() != null) {
				messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ACCEPTING_ORDERING.name(), messaging.getSender().getId()) + croissant.getName() + "?", ACCEPT_ORDERING_PAYLOAD.name() + "?" + croissantId, "&");
				return;

			}
		}

		createOrdering(user, croissant, messaging);
	}

	private void createOrdering(User user, Croissant croissant, Messaging messaging) {
		CustomerOrdering customerOrdering = new CustomerOrdering();
		customerOrdering.setPrice(0);
		SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
		supportEntity.setCount(Integer.parseInt(croissant.getId().toString()));
		supportEntityRepositoryService.saveAndFlush(supportEntity);
		customerOrdering.setName(user.getName() + " " + user.getLastName());
		user.addCustomerOrdering(customerOrdering);
		customerOrderingRepositoryService.saveAndFlush(customerOrdering);
		userRepositoryService.saveAndFlush(user);
		if (userEventService.isUser(user)) {

			isExistsUser(customerOrdering, messaging);
		} else {
			orderingFinalist(messaging);
		}
	}


	private void orderingFinalist(Messaging messaging) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		CustomerOrdering ordering = customerOrderingRepositoryService.findTopByUser(user);


		if (ordering.getPhoneNumber() == null) {
			parsePhoneNumber(messaging, ordering, user);
		} else if (ordering.getAddress() == null) {
			parseAddress(messaging, ordering, user);


		} else if (ordering.getTime() == null) {
			parseTime(messaging, ordering, user);


		} else {
			errorAction(messaging);
		}
	}


	private void isExistsUser(CustomerOrdering customerOrdering, Messaging messaging) {

		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		customerOrdering.setPhoneNumber(user.getPhoneNumber());
		customerOrdering.setAddress(user.getAddress());
		customerOrderingRepositoryService.saveAndFlush(customerOrdering);
		String text = user.getName() + ", " + recognizeService.recognize(ADDRESS_LOCATION_QUESTION.name(), messaging.getSender().getId()) + ": (" + user.getAddress() + ")?";
		messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), text, ADDRESS_PAYLOAD.name(), "?");

	}


	private void errorAction(Messaging messaging) {
		User customer1 = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		customer1.setStatus(null);
		userRepositoryService.saveAndFlush(customer1);
		messageSenderService.errorMessage(messaging.getSender().getId());
		if (customerOrderingRepositoryService.findTop().getCroissants().isEmpty()) {
			customerOrderingRepositoryService.delete(customerOrderingRepositoryService.findTop());
		}
	}

	private void parseTime(Messaging messaging, CustomerOrdering ordering, User user) {

		if (!user.getStatus().equals(ASK_TIME.name())) {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(TIME_OF_ORDERING.name(), messaging.getSender().getId()), messaging.getSender().getId());
			userEventService.changeStatus(messaging, ASK_TIME.name());
		} else if (TextFormatter.isCorrectTime(messaging.getMessage().getText())) {


			ordering.setTime(messaging.getMessage().getText());
			customerOrderingRepositoryService.saveAndFlush(ordering);
			userEventService.changeStatus(messaging, null);


			SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
			Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(supportEntity.getCount().toString()));
			messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ACCEPTING_ORDERING.name(), messaging.getSender().getId()) + croissant.getName() + "?", ACCEPT_ORDERING_PAYLOAD.name() + "?" + croissant.getId(), "&");
			supportEntity.setCount(null);
			supportEntityRepositoryService.saveAndFlush(supportEntity);
		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_TIME.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(TIME_OF_ORDERING.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}
	}


	private void parseAddress(Messaging messaging, CustomerOrdering ordering, User user) {
		if (user.getAddress() != null && messaging.getMessage().getQuickReply() == null && !user.getStatus().equals(ASK_ADDRESS.name())) {
			ordering.setAddress(user.getAddress());
			customerOrderingRepositoryService.saveAndFlush(ordering);
			orderingFinalist(messaging);

		} else if (!user.getStatus().equals(ASK_ADDRESS.name())) {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());
			userEventService.changeStatus(messaging, ASK_ADDRESS.name());
		} else if (TextFormatter.isCorrectAddress(messaging.getMessage().getText())) {
			ordering.setAddress(messaging.getMessage().getText());
			customerOrderingRepositoryService.saveAndFlush(ordering);
			userEventService.changeStatus(messaging, ORDERING.name());
			user.setAddress(ordering.getAddress());
			userRepositoryService.saveAndFlush(user);

			orderingFinalist(messaging);

		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}

	}


	private void parsePhoneNumber(Messaging messaging, CustomerOrdering ordering, User user) {
		if (user.getPhoneNumber() != null) {
			ordering.setPhoneNumber(user.getPhoneNumber());
			customerOrderingRepositoryService.saveAndFlush(ordering);
			orderingFinalist(messaging);

		} else if (!user.getStatus().equals(ASK_PHONE_NUMBER.name())) {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
			userEventService.changeStatus(messaging, ASK_PHONE_NUMBER.name());
		} else if (TextFormatter.isPhoneNumber(messaging.getMessage().getText())) {
			ordering.setPhoneNumber(messaging.getMessage().getText());
			customerOrderingRepositoryService.saveAndFlush(ordering);
			userEventService.changeStatus(messaging, ORDERING.name());
			if (user.getPhoneNumber() == null) {
				user.setPhoneNumber(ordering.getPhoneNumber());
				userRepositoryService.saveAndFlush(user);
			}
			orderingFinalist(messaging);

		} else {
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
			messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
		}

	}


}
