package com.example.demo.service.telegramService;

import com.example.demo.dto.telegram.Message;

public interface TelegramMessageParserService {
    public void parseMessage(Message message);
}
