package com.example.demo.services.adminPanelService.impl;

import com.example.demo.enums.messengerEnums.Objects;
import com.example.demo.enums.telegramEnums.BotCommands;
import com.example.demo.models.messanger.Shell;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.adminPanelService.BotCommandParseHelperService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ResourceBundle;

@Service
public class BotCommandParseHelperServiceImpl implements BotCommandParseHelperService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Value("${server.url}")
    private String SERVER_URL;

    @Value("${app.verify.token}")
    private String VER_TOK;
    @Value("${subscription.url}")
    private String SUBSCRIPTION_URL;
    private static final Logger logger = Logger.getLogger(BotCommandParseHelperServiceImpl.class);
    @Override
    public void helpInvokeBotHelpCommand(Message message) {
        StringBuilder helpMessage = new StringBuilder();
        for(BotCommands command: BotCommands.values()){
            if(command!=BotCommands.HELP)
            helpMessage.append("/"+command.name().toLowerCase()+" - "+ResourceBundle.getBundle("botCommands").getString(command.name())+"\n");
        }
        telegramMessageSenderService.simpleMessage(helpMessage.toString(),message);
    }

    @Override
    public void helpSetUpMessenger(Message message) {
        Shell setMessengerWebHook = new Shell();
        setMessengerWebHook.setCallbackUrl(SERVER_URL+"/WebHook");
        setMessengerWebHook.setVerToken(VER_TOK);
        setMessengerWebHook.setObject(Objects.page);
        setMessengerWebHook.setFields(new String[]{"messages","messaging_postbacks"});
        try {
            ResponseEntity<?> messengerWebhook = new RestTemplate().postForEntity(SUBSCRIPTION_URL,setMessengerWebHook,Object.class);
            logger.debug("Messenger webhook:"+messengerWebhook.getBody());
            telegramMessageSenderService.simpleMessage("Facebook messenger: "+messengerWebhook.getBody().toString(),message);
        }
        catch (Exception ex){
            logger.warn(ex);
            telegramMessageSenderService.simpleMessage(ex.getMessage(),message);
        }

    }
}
