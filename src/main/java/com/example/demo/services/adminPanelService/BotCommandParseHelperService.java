package com.example.demo.services.adminPanelService;


import telegram.CallBackQuery;
import telegram.Message;

public interface BotCommandParseHelperService {
	public void helpInvokeBotHelpCommand(Message message);

	void helpSetUpMessenger(Message message);

	public void helpGetListOfOrdering(CallBackQuery callBackQuery);

	public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
