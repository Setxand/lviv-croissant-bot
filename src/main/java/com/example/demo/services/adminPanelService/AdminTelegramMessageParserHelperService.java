package com.example.demo.services.adminPanelService;

import com.example.demo.models.telegram.Message;

public interface AdminTelegramMessageParserHelperService {
    public void helpSetRole(Message message);
    public void helpRoleQuestion(Message message);
    public void helpChangeHelloMessage(Message message);

}
