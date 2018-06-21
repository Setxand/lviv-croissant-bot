package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;
import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface BotCommandParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);
    void helpSetUpMessenger(Message message);
    public void helpGetListOfOrdering(CallBackQuery callBackQuery);
    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
