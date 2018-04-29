package com.example.demo.services.adminPanelServce.impl;

import com.example.demo.enums.Platform;
import com.example.demo.models.telegram.Update;
import com.example.demo.services.adminPanelServce.AdminCallBackParserService;
import com.example.demo.services.adminPanelServce.AdminPanelupdateParserService;
import com.example.demo.services.adminPanelServce.AdminTelegramMessageParserService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelUpdateParserServiceImpl implements AdminPanelupdateParserService {



    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private AdminCallBackParserService adminCallBackParserService;
    @Autowired
    private AdminTelegramMessageParserService adminTelegramMessageParserService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;

    @Override
    public void parseUpdate(Update update) {


        try {
            if (update.getCallBackQuery() != null) {
                update.getCallBackQuery().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);
                adminCallBackParserService.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);
                adminTelegramMessageParserService.parseMessage(update.getMessage());
            }
        } catch (Exception ex) {
            try {
                telegramMessageSenderService.errorMessage(update.getMessage().getChat().getId());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }


    }
}
