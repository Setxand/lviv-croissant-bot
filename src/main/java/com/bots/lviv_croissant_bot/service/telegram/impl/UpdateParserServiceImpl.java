package com.bots.lviv_croissant_bot.service.telegram.impl;

import com.bots.lviv_croissant_bot.dto.telegram.Update;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.CallBackParserService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageParserService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
import com.bots.lviv_croissant_bot.service.telegram.UpdateParserService;
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
            if(update.getCallBackQuery()!=null){
                callBackParserServiceService.parseCallBackQuery(update.getCallBackQuery());
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
