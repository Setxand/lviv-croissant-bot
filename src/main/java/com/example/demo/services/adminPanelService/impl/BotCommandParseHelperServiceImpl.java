package com.example.demo.services.adminPanelService.impl;

import com.example.demo.client.TelegramClientEx;
import com.example.demo.constcomponent.BotCommands;
import com.example.demo.constcomponent.Platform;
import com.example.demo.constcomponent.messengerEnums.Objects;
import com.example.demo.constcomponent.messengerEnums.Roles;
import com.example.demo.entity.lvivCroissants.Croissant;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.model.messanger.Shell;
import com.example.demo.services.adminPanelService.BotCommandParseHelperService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegram.CallBackQuery;
import telegram.Markup;
import telegram.Message;
import telegram.button.InlineKeyboardButton;
import telegram.button.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.constcomponent.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constcomponent.telegramEnums.CallBackData.*;

@Service
public class BotCommandParseHelperServiceImpl implements BotCommandParseHelperService {
	private static final Logger logger = Logger.getLogger(BotCommandParseHelperServiceImpl.class);
	@Autowired private TelegramClientEx telegramClient;
	@Autowired private TelegramUserRepositoryService telegramUserRepositoryService;
	@Autowired private CustomerOrderingRepositoryService customerOrderingRepositoryService;
	@Autowired private CroissantRepositoryService croissantRepositoryService;
	@Value("${server.url}")
	private String SERVER_URL;
	@Value("${app.verify.token}")
	private String VER_TOK;
	@Value("${subscription.url}")
	private String SUBSCRIPTION_URL;
	@Value("${picture.ordering}")
	private String PICTURE_ORDERING;

	@Override
	public void helpInvokeBotHelpCommand(Message message) {
		StringBuilder helpMessage = new StringBuilder();
		for (BotCommands command : BotCommands.values()) {
			if (command != BotCommands.HELP && command != BotCommands.START)
				helpMessage.append("/" + command.name().toLowerCase() + " - " + ResourceBundle.getBundle("botCommands").getString(command.name()) + "\n");
		}
		telegramClient.simpleMessage(helpMessage.toString(), message);
	}

	@Override
	public void helpSetUpMessenger(Message message) {
		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() != Roles.ADMIN) {
			telegramClient.noEnoughPermissions(message);
			return;
		}
		Shell setMessengerWebHook = new Shell();
		setMessengerWebHook.setCallbackUrl(SERVER_URL + "/WebHook");
		setMessengerWebHook.setVerToken(VER_TOK);
		setMessengerWebHook.setObject(Objects.page);
		setMessengerWebHook.setFields(new String[]{"messages", "messaging_postbacks"});
		makeRequestToFacebook(message, setMessengerWebHook);

	}

	@Override
	public void helpGetListOfOrdering(CallBackQuery callBackQuery) {
		String data = TextFormatter.ejectPaySinglePayload(callBackQuery.getData());
		TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
		String uah = ResourceBundle.getBundle("dictionary").getString(CURRENCY.name());
		String getOrder = ResourceBundle.getBundle("dictionary").getString(GETTING_ORDER.name());
		String completeOrder = ResourceBundle.getBundle("dictionary").getString(COMPLETE_ORDERING.name());
		List<CustomerOrdering> customerOrderings = customerOrderingRepositoryService.findAll();
		StringBuilder croissants = new StringBuilder();
		for (CustomerOrdering customerOrdering : customerOrderings) {
			if (customerOrdering.getCourier() == null && data.equals(LIST_OF_ORDERING_DATA.name())) {
				Markup markup = new InlineKeyboardMarkup(Arrays.asList(Arrays.asList(new InlineKeyboardButton(getOrder, GET_ORDER_DATA.name() + "?" + customerOrdering.getId()))));
				getListOfOrderings(callBackQuery, customerOrdering, uah, markup, croissants);
			} else if (customerOrdering.getCourier() == tUser && data.equals(LIST_OF_COMPLETE_ORDERING_DATA.name()) && customerOrdering.getCompletedTime() == null) {
				Markup markup = new InlineKeyboardMarkup(Arrays.asList(Arrays.asList(new InlineKeyboardButton(completeOrder, COMPLETE_ORDER_DATA.name() + "?" + customerOrdering.getId()))));
				getListOfOrderings(callBackQuery, customerOrdering, uah, markup, croissants);
			}
		}
	}

	@Override
	public void helpCompleteOrderData(CallBackQuery callBackQuery) {
		String orderId = TextFormatter.ejectSingleVariable(callBackQuery.getData());
		CustomerOrdering customerOrdering = customerOrderingRepositoryService.findOne(Long.parseLong(orderId));
		TUser tUser = customerOrdering.getTUser();
		callBackQuery.getMessage().getChat().setId(tUser.getChatId());
		callBackQuery.getMessage().setPlatform(Platform.COMMON_BOT);
		String text = ResourceBundle.getBundle("dictionary").getString(RECEiVE_ORDER.name());
		telegramClient.simpleQuestion(QUESTION_COMPLETE_DATA, "?" + orderId + "&", text, callBackQuery.getMessage());
	}

	private void makeRequestToFacebook(Message message, Shell setMessengerWebHook) {
		try {
			ResponseEntity<?> messengerWebhook = new RestTemplate().postForEntity(SUBSCRIPTION_URL, setMessengerWebHook, Object.class);
			logger.debug("Messenger webhook:" + messengerWebhook.getBody());
			telegramClient.simpleMessage("Facebook messenger: " + messengerWebhook.getBody().toString() + " /help", message);
		} catch (Exception ex) {
			logger.warn(ex);
			telegramClient.simpleMessage(ex.getMessage(), message);
		}
	}

	private void getListOfOrderings(CallBackQuery callBackQuery, CustomerOrdering customerOrdering, String uah, Markup markup, StringBuilder croissants) {
		croissants.setLength(0);
		for (String orderId : customerOrdering.getCroissants()) {
			long id = Long.parseLong(orderId);
			Croissant croissant = croissantRepositoryService.findOne(id);
			if (croissant.getType().equals(OWN.name())) {
				croissants.append(croissant);
				continue;
			}
			croissants.append(croissant.getName() + "; ");
		}
		String caption = customerOrdering.getId() + ". " + "time: " + customerOrdering.getTime() + "\naddress: " + customerOrdering.getAddress() + "" +
				"\nphone number: " + customerOrdering.getPhoneNumber() + "\n" + croissants + "\n" +
				customerOrdering.getPrice() + uah;
		telegramClient.sendPhoto(PICTURE_ORDERING, caption, markup, callBackQuery.getMessage());
	}
}
