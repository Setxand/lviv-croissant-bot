package com.example.demo.services.adminPanelService;

import com.example.demo.models.telegram.Message;

public interface BotCommendParseHelperService {
    public void helpInvokeBotHelpCommand(Message message);

    void helpSetUpMessenger(Message message);
}
