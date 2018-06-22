package com.bots.lviv_croissant_bot.service.telegram.impl;

import com.bots.lviv_croissant_bot.dto.telegram.Update;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.CallBackParser;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageParser;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSender;
import com.bots.lviv_croissant_bot.service.telegram.UpdateParser;
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
