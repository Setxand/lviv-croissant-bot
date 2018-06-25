package com.bots.lvivcroissantbot.service.adminpanel.impl;

import com.bots.lvivcroissantbot.constantenum.Platform;
import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.dto.telegram.Update;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.service.adminpanel.AdminCallBackParserService;
import com.bots.lvivcroissantbot.service.adminpanel.AdminPanelUpdateParserService;
import com.bots.lvivcroissantbot.service.adminpanel.AdminTelegramMessageParserService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
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
                if (tUser.getUser().getRole() == Role.CUSTOMER) {
                    telegramMessageSenderService.simpleMessage("U are not a personal of Lviv croissants!", update.getCallBackQuery().getMessage());
                    return;
                }
                adminCallBackParserService.parseAdminCallBackQuery(update.getCallBackQuery());
            } else if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.TELEGRAM_ADMIN_PANEL_BOT);

                tUser = telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId());
                if (tUser.getUser().getRole() == Role.CUSTOMER && !update.getMessage().getText().equals("/start")) {
                    telegramMessageSenderService.simpleMessage("U are not a personal of Lviv croissants!", update.getMessage());
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
