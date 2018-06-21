package com.bots.lvivCroissantBot.service.adminPanel.impl;

import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.Platform;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role;
import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.adminPanel.AdminCallBackParser;
import com.bots.lvivCroissantBot.service.adminPanel.AdminPanelUpdateParser;
import com.bots.lvivCroissantBot.service.adminPanel.AdminTelegramMessageParser;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelUpdateParserImpl implements AdminPanelUpdateParser {


    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private AdminCallBackParser adminCallBackParser;
    @Autowired
    private AdminTelegramMessageParser adminTelegramMessageParser;
    @Autowired
    private TelegramMessageSender telegramMessageSender;

    @Override
    public void parseUpdate(Update update) {
        TUser tUser = null;

        try {

            if (update.getCallBackQuery() != null) {
                update.getCallBackQuery().getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getCallBackQuery().getMessage().getChat().getId());
                if(tUser.getUser().getRole()==Role.CUSTOMER){
                    telegramMessageSender.simpleMessage("U are not a personal of Lviv croissants!",update.getCallBackQuery().getMessage());
                    return;
                }
                adminCallBackParser.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId());
                if(tUser.getUser().getRole()==Role.CUSTOMER && !update.getMessage().getText().equals("/start")){
                    telegramMessageSender.simpleMessage("U are not a personal of Lviv croissants!",update.getMessage());
                    return;
                }
                adminTelegramMessageParser.parseMessage(update.getMessage());

            }

        } catch (Exception ex) {
            try {
                telegramMessageSender.errorMessage(update.getMessage());
                telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ex.printStackTrace();
        }


    }
}
