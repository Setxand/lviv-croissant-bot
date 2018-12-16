package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.Platform;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.models.telegram.Update;
import com.example.demo.services.adminPanelService.AdminCallBackParserService;
import com.example.demo.services.adminPanelService.AdminPanelUpdateParserService;
import com.example.demo.services.adminPanelService.AdminTelegramMessageParserService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelUpdateParserServiceImpl implements AdminPanelUpdateParserService {


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
        TUser tUser = null;

        try {

            if (update.getCallBackQuery() != null) {
                update.getCallBackQuery().getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getCallBackQuery().getMessage().getChat().getId());
                if(tUser.getRole()==Roles.CUSTOMER){
                    telegramMessageSenderService.simpleMessage("U are not a personal of Lviv croissants!",update.getCallBackQuery().getMessage());
                    return;
                }
                adminCallBackParserService.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId());
                if(tUser.getRole()==Roles.CUSTOMER && !update.getMessage().getText().equals("/start")){
                    telegramMessageSenderService.simpleMessage("U are not a personal of Lviv croissants!",update.getMessage());
                    return;
                }
                adminTelegramMessageParserService.parseMessage(update.getMessage());

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