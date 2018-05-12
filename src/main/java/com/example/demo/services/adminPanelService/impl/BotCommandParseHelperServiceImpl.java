package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.messengerEnums.Objects;
import com.example.demo.enums.BotCommands;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.models.messanger.Shell;
import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.buttons.InlineKeyboardButton;
import com.example.demo.models.telegram.buttons.InlineKeyboardMarkup;
import com.example.demo.models.telegram.buttons.Markup;
import com.example.demo.repository.TelegramUserRepository;
import com.example.demo.services.adminPanelService.BotCommandParseHelperService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.CroissantsFillingRepositoryService;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.telegramEnums.CallBackData.MAKE_ORDER_DATA;

@Service
public class BotCommandParseHelperServiceImpl implements BotCommandParseHelperService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Value("${server.url}")
    private String SERVER_URL;
    @Value("${app.verify.token}")
    private String VER_TOK;
    @Value("${subscription.url}")
    private String SUBSCRIPTION_URL;
    @Value("${picture.ordering}")
    private String PICTURE_ORDERING;
    private static final Logger logger = Logger.getLogger(BotCommandParseHelperServiceImpl.class);


    @Override
    public void helpInvokeBotHelpCommand(Message message) {
        StringBuilder helpMessage = new StringBuilder();
        for(BotCommands command: BotCommands.values()){
            if(command!=BotCommands.HELP && command!=BotCommands.START)
            helpMessage.append("/"+command.name().toLowerCase()+" - "+ResourceBundle.getBundle("botCommands").getString(command.name())+"\n");
        }
        telegramMessageSenderService.simpleMessage(helpMessage.toString(),message);
    }

    @Override
    public void helpSetUpMessenger(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if(tUser.getRole()!= Roles.ADMIN){
            telegramMessageSenderService.noEnoughPermissions(message);
            return;
        }
        Shell setMessengerWebHook = new Shell();
        setMessengerWebHook.setCallbackUrl(SERVER_URL+"/WebHook");
        setMessengerWebHook.setVerToken(VER_TOK);
        setMessengerWebHook.setObject(Objects.page);
        setMessengerWebHook.setFields(new String[]{"messages","messaging_postbacks"});
        makeRequestToFacebook(message,setMessengerWebHook);

    }

    private void makeRequestToFacebook(Message message, Shell setMessengerWebHook) {
        try {
            ResponseEntity<?> messengerWebhook = new RestTemplate().postForEntity(SUBSCRIPTION_URL,setMessengerWebHook,Object.class);
            logger.debug("Messenger webhook:"+messengerWebhook.getBody());
            telegramMessageSenderService.simpleMessage("Facebook messenger: "+messengerWebhook.getBody().toString()+" /help",message);
        }
        catch (Exception ex){
            logger.warn(ex);
            telegramMessageSenderService.simpleMessage(ex.getMessage(),message);
        }
    }

    @Override
    public void helpGetListOfOrdering(CallBackQuery callBackQuery) {
        String uah = ResourceBundle.getBundle("dictionary").getString(CURRENCY.name());
        String makeOrder = ResourceBundle.getBundle("dictionary").getString(GETTING_ORDER.name());
        List<CustomerOrdering> customerOrderings = customerOrderingRepositoryService.findAll();
        StringBuilder croissants = new StringBuilder();
        for(CustomerOrdering customerOrdering: customerOrderings){
            croissants.setLength(0);
            for(String orderId:customerOrdering.getCroissants()){
                long id = Long.parseLong(orderId);
                Croissant croissant = croissantRepositoryService.findOne(id);
                if(croissant.getType().equals(OWN.name())){
                    croissants.append(croissant);
                    continue;
                }
                croissants.append(croissant.getName()+"; ");
            }
            String caption = customerOrdering.getId()+". "+"time: "+customerOrdering.getTime()+"\naddress: "+customerOrdering.getAddress()+"" +
            "\nphone number: "+customerOrdering.getPhoneNumber()+"\n"+croissants+"\n" +
                    customerOrdering.getPrice()+uah;
            Markup markup = new InlineKeyboardMarkup(Arrays.asList(Arrays.asList(new InlineKeyboardButton(makeOrder,MAKE_ORDER_DATA.name()+"?"+customerOrdering.getId()))));
            telegramMessageSenderService.sendPhoto(PICTURE_ORDERING,caption,markup,callBackQuery.getMessage());
        }
    }
}
