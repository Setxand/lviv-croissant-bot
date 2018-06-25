package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface AdminTelegramMessageParserHelperService {
    public void helpSetRole(Message message);

    public void helpRoleQuestion(Message message);

    public void helpChangeHelloMessage(Message message);

}
