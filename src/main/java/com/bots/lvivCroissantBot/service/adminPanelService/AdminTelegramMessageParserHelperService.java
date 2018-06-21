package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface AdminTelegramMessageParserHelperService {
    public void helpSetRole(Message message);
    public void helpRoleQuestion(Message message);
    public void helpChangeHelloMessage(Message message);

}
