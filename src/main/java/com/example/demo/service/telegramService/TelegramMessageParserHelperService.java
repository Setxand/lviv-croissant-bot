package com.example.demo.service.telegramService;

import com.example.demo.dto.telegram.Message;

public interface TelegramMessageParserHelperService {
    void helpStart(Message message);

    void helpDeleteOrderings(Message message);

    void helpCreateOwnCroissant(Message message);

    void helpEnterPhoneInStart(Message message);
}
