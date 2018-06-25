package com.bots.lvivcroissantbot.service.telegram.impl;

import com.bots.lvivcroissantbot.dto.telegram.Update;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.telegram.CallBackParserService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageParserService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import com.bots.lvivcroissantbot.service.telegram.UpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateParserServiceImpl implements UpdateParserService {
    @Autowired
    private TelegramMessageParserService telegramMessageParserService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CallBackParserService callBackParserServiceService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;

    @Override
    public void parseUpdate(Update update) {
        try {
            if (update.getCallBackQuery() != null) {
                callBackParserServiceService.parseCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                telegramMessageParserService.parseMessage(update.getMessage());
            }
        } catch (Exception ex) {
            try {
                telegramMessageSenderService.errorMessage(update.getMessage());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }

    }
}
