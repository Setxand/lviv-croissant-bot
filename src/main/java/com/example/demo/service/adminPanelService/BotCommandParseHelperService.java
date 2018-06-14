package com.example.demo.service.adminPanelService;

import com.example.demo.dto.telegram.CallBackQuery;
import com.example.demo.dto.telegram.Message;

public interface BotCommandParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);
    void helpSetUpMessenger(Message message);
    public void helpGetListOfOrdering(CallBackQuery callBackQuery);
    public void helpCompleteOrderData(CallBackQuery callBackQuery);
}
