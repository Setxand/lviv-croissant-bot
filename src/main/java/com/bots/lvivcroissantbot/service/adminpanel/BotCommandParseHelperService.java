package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;
import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface BotCommandParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);

    void helpSetUpMessenger(Message message);

    public void helpGetListOfOrdering(CallBackQuery callBackQuery);

    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
