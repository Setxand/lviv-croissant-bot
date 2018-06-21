package com.bots.lvivCroissantBot.service.adminPanelService.impl;

import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import com.bots.lvivCroissantBot.constantEnum.Platform;
import com.bots.lvivCroissantBot.constantEnum.messengerEnums.Role;
import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.adminPanelService.AdminCallBackParserService;
import com.bots.lvivCroissantBot.service.adminPanelService.AdminPanelUpdateParserService;
import com.bots.lvivCroissantBot.service.adminPanelService.AdminTelegramMessageParserService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegramService.TelegramMessageSenderService;
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
