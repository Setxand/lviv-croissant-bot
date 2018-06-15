package com.example.demo.service.telegramService;

import com.example.demo.dto.telegram.Message;

public interface TelegramMessageParserHelperService {
    public void helpStart(Message message);
    public void helpDeleteOrderings(Message message);
    public void helpCreateOwnCroissant(Message message);

}
