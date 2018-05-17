package com.example.demo.services.adminPanelService;

import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;

public interface BotCommandParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);
    void helpSetUpMessenger(Message message);
    public void helpGetListOfOrdering(CallBackQuery callBackQuery);
    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
