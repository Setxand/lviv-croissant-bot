package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;
import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface BotCommandParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);
    void helpSetUpMessenger(Message message);
    public void helpGetListOfOrdering(CallBackQuery callBackQuery);
    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
