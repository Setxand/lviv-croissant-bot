package com.example.demo.service.adminPanelService;

import com.example.demo.dto.telegram.Message;

public interface AdminTelegramMessageParserHelperService {
    public void helpSetRole(Message message);
    public void helpRoleQuestion(Message message);
    public void helpChangeHelloMessage(Message message);

}
