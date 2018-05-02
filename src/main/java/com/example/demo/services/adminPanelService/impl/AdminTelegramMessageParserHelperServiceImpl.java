package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.models.telegram.Message;
import com.example.demo.repository.TelegramUserRepository;
import com.example.demo.services.adminPanelService.AdminTelegramMessageParserHelperService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.enums.telegramEnums.CallBackData.SETTING_ROLE_STATUS;

@Service
public class AdminTelegramMessageParserHelperServiceImpl implements AdminTelegramMessageParserHelperService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Override
    public void helpSetRole(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()){
            case SETTING_ADMIN_STATUS:
                settingAdminStatus(message,tUser);
                break;
        }

    }

    private void settingAdminStatus(Message message, TUser tUserAdm) {
        try {
            TUser tUser = telegramUserRepositoryService.findByUserName(message.getFrom().getUserName());
            String text = "Are you sure you want to set Admin`s role for"+tUser.getName()+" "+tUser.getLastName()+" with username \""+tUser.getUserName()+"\"?";
            telegramMessageSenderService.simpleQuestion(SETTING_ROLE_STATUS,"?"+tUser.getUserName(),text,message);
        }
        catch (Exception ex){
            telegramUserRepositoryService.changeStatus(tUserAdm,null);
            ex.printStackTrace();
        }

    }
}
