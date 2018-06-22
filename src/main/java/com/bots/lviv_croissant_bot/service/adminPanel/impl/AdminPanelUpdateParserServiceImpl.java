package com.bots.lviv_croissant_bot.service.adminPanel.impl;

import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.constantEnum.Platform;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
import com.bots.lviv_croissant_bot.dto.telegram.Update;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminCallBackParserService;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminPanelUpdateParserService;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminTelegramMessageParserService;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
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
                if(tUser.getUser().getRole()==Role.CUSTOMER){
                    telegramMessageSenderService.simpleMessage("U are not a personal of Lviv croissants!",update.getCallBackQuery().getMessage());
                    return;
                }
                adminCallBackParserService.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId());
                if(tUser.getUser().getRole()==Role.CUSTOMER && !update.getMessage().getText().equals("/start")){
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
