package com.example.demo.services.telegramService;

import com.example.demo.models.telegram.Message;

public interface TelegramMessageParserHelperService {
    public void helpStart(Message message);
    public void helpDeleteOrderings(Message message);
    public void helpCreateOwnCroissant(Message message);
    public void helpSetUpMessenger(Message message);
}
