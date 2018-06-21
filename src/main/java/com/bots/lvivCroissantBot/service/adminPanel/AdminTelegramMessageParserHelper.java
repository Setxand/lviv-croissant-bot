package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface AdminTelegramMessageParserHelper {
    public void helpSetRole(Message message);
    public void helpRoleQuestion(Message message);
    public void helpChangeHelloMessage(Message message);

}
