package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;
import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface BotCommandParseHelper {
    public void helpInvokeBotHelpCommand(Message message);
    void helpSetUpMessenger(Message message);
    public void helpGetListOfOrdering(CallBackQuery callBackQuery);
    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
