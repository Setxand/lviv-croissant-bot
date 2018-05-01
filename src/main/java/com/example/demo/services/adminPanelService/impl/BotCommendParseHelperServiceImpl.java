package com.example.demo.services.adminPanelService.impl;

import com.example.demo.enums.telegramEnums.BotCommands;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.adminPanelService.BotCommendParseHelperService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class BotCommendParseHelperServiceImpl implements BotCommendParseHelperService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Override
    public void helpInvokeBotHelpCommand(Message message) {
        StringBuilder helpMessage = new StringBuilder();
        for(BotCommands command: BotCommands.values()){
            if(command!=BotCommands.HELP)
            helpMessage.append("/"+command.name().toLowerCase()+" - "+ResourceBundle.getBundle("botCommands").getString(command.name())+"\n");
        }
        telegramMessageSenderService.simpleMessage(helpMessage.toString(),message);
    }
}
