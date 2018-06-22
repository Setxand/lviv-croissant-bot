package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface AdminTelegramMessageParserHelper {
    public void helpSetRole(Message message);
    public void helpRoleQuestion(Message message);
    public void helpChangeHelloMessage(Message message);

}
