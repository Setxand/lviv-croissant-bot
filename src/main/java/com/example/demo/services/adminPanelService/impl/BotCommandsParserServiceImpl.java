package com.example.demo.services.adminPanelService.impl;

import com.example.demo.constcomponent.BotCommands;
import com.example.demo.constcomponent.messengerEnums.Roles;
import com.example.demo.constcomponent.telegramEnums.TelegramUserStatus;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.services.adminPanelService.BotCommandParseHelperService;
import com.example.demo.services.adminPanelService.BotCommandsParserService;
import com.example.demo.services.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageParserHelperService;
import com.example.demo.test.TelegramClientEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import telegram.Message;
import telegram.button.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.constcomponent.messengerEnums.Roles.ADMIN;
import static com.example.demo.constcomponent.messengerEnums.Roles.COURIER;
import static com.example.demo.constcomponent.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constcomponent.telegramEnums.CallBackData.*;
import static com.example.demo.constcomponent.telegramEnums.TelegramUserStatus.ASKING_TYPE_STATUS;

@Service
public class BotCommandsParserServiceImpl implements BotCommandsParserService {
	@Autowired
	private TelegramUserRepositoryService telegramUserRepositoryService;
	@Autowired
	private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
	@Autowired
	private BotCommandParseHelperService botCommandParseHelperService;
	@Autowired
	private TelegramClientEx telegramCLient;
	@Autowired
	private TelegramGetMenuEventService telegramGetMenuEventService;
	@Autowired
	private TelegramMessageParserHelperService telegramMessageParserHelperService;

	@Override
	public void parseBotCommand(Message message) {
		StringBuilder command = new StringBuilder(message.getText()).deleteCharAt(0);
		switch (BotCommands.valueOf(command.toString().toUpperCase())) {
			case FILLING:
				filling(message);
				break;
			case ADD:
				add(message);
				break;
			case HELP:
				help(message);
				break;
			case SETUPMESSENGER:
				setUpMessenger(message);
				break;
			case DELETECROISSANT:
				deleteCroissant(message);
				break;
			case ADMINPANEL:
				adminPanel(message);
				break;
			case START:
				telegramMessageParserHelperService.helpStart(message);
				break;
			case COURIERACTIONS:
				courierActions(message);
				break;
			default:
				telegramCLient.errorMessage(message);
				break;
		}

	}

	private void courierActions(Message message) {
		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() == ADMIN || tUser.getRole() == COURIER) {
			String listOfOrdering = ResourceBundle.getBundle("dictionary").getString(ORDERING_LIST.name());
			String listOfOwnOrdering = ResourceBundle.getBundle("dictionary").getString(COMPLETE_ORDERING.name().toUpperCase());
			List<InlineKeyboardButton> buttons = Arrays.asList(new InlineKeyboardButton(listOfOrdering, LIST_OF_ORDERING_DATA.name()),
					new InlineKeyboardButton(listOfOwnOrdering, LIST_OF_COMPLETE_ORDERING_DATA.name()));
			String courierActions = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
			telegramCLient.sendInlineButtons(courierActions, message, buttons.toArray(new InlineKeyboardButton[0]));

		} else {
			telegramCLient.noEnoughPermissions(message);
		}
	}


	private void adminPanel(Message message) {
		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() != ADMIN) {
			telegramCLient.noEnoughPermissions(message);
			return;
		}
		List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(new InlineKeyboardButton("Set role", SET_ROLE_DATA.name()),
				new InlineKeyboardButton("Change hello message", SET_HELLO_MESSAGE_DATA.name())));
		String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
		telegramCLient.sendInlineButtons(text, message, buttons.toArray(new InlineKeyboardButton[0]));
	}

	private void deleteCroissant(Message message) {

		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() != ADMIN && tUser.getRole() != Roles.PERSONAL) {
			telegramCLient.noEnoughPermissions(message);
			return;
		}
		telegramUserRepositoryService.changeStatus(tUser, ASKING_TYPE_STATUS);
		telegramGetMenuEventService.getMenu(message);
	}

	private void setUpMessenger(Message message) {

		botCommandParseHelperService.helpSetUpMessenger(message);
	}

	private void help(Message message) {
		botCommandParseHelperService.helpInvokeBotHelpCommand(message);
	}

	private void add(Message message) {
		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() != Roles.COURIER) {
			telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_CROISSANT_STATUS);
			telegramAddingRecordingsEventService.addCroissant(message);
		} else
			telegramCLient.noEnoughPermissions(message);

	}

	private void filling(Message message) {

		TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
		if (tUser.getRole() != Roles.COURIER) {
			telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_FILLING_STATUS);
			telegramAddingRecordingsEventService.addFilling(message);
		} else
			telegramCLient.noEnoughPermissions(message);
	}
}
