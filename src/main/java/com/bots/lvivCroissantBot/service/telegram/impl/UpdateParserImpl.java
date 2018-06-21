package com.bots.lvivCroissantBot.service.telegram.impl;

import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.CallBackParser;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageParser;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import com.bots.lvivCroissantBot.service.telegram.UpdateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateParserImpl implements UpdateParser {
    @Autowired
    private TelegramMessageParser telegramMessageParser;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private CallBackParser callBackParserService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Override
    public void parseUpdate(Update update) {
        try {
            if(update.getCallBackQuery()!=null){
                callBackParserService.parseCallBackQuery(update.getCallBackQuery());
            }
            else if(update.getMessage()!=null){
                telegramMessageParser.parseMessage(update.getMessage());
            }
        }
        catch (Exception ex){
            try {
                telegramMessageSender.errorMessage(update.getMessage());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()),null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }

    }
}
