package com.example.demo.service.adminPanelService;

import com.example.demo.dto.telegram.Message;

public interface AdminTelegramMessageParserService {
    public void parseMessage(Message message);
}
