package com.example.demo.service.telegramService.impl;

import com.example.demo.dto.telegram.Update;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.telegramService.CallBackParserService;
import com.example.demo.service.telegramService.TelegramMessageParserService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import com.example.demo.service.telegramService.UpdateParserService;
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
