package com.bots.lvivCroissantBot.service.telegramService.impl;

import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.peopleRegisterService.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegramService.CallBackParserService;
import com.bots.lvivCroissantBot.service.telegramService.TelegramMessageParserService;
import com.bots.lvivCroissantBot.service.telegramService.TelegramMessageSenderService;
import com.bots.lvivCroissantBot.service.telegramService.UpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateParserServiceImpl implements UpdateParserService {
    @Autowired
    private TelegramMessageParserService telegramMessageParserService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CallBackParserService callBackParserService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Override
    public void parseUpdate(Update update) {
        try {
            if(update.getCallBackQuery()!=null){
                callBackParserService.parseCallBackQuery(update.getCallBackQuery());
            }
            else if(update.getMessage()!=null){
                telegramMessageParserService.parseMessage(update.getMessage());
            }
        }
        catch (Exception ex){
            try {
                telegramMessageSenderService.errorMessage(update.getMessage());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()),null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }

    }
}