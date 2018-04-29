package com.example.demo.services.adminPanelServce.impl;

import com.example.demo.models.telegram.Update;
import com.example.demo.services.adminPanelServce.AdminCallBackParserService;
import com.example.demo.services.adminPanelServce.AdminPanelupdateParserService;
import com.example.demo.services.adminPanelServce.AdminTelegramMessageParserService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelupdateParserServiceImpl implements AdminPanelupdateParserService {



    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private AdminCallBackParserService adminCallBackParserService;
    @Autowired
    private AdminTelegramMessageParserService adminTelegramMessageParserService;

    @Override
    public void parseUpdate(Update update) {


        try {
            if (update.getCallBackQuery() != null) {
                adminCallBackParserService.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                adminTelegramMessageParserService.parseMessage(update.getMessage());
            }
        } catch (Exception ex) {
            try {
//                telegramMessageSenderService.errorMessage(update.getMessage().getChat().getId());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }


    }
}
