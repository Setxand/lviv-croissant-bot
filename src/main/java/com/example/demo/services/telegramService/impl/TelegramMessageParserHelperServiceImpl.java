package com.example.demo.services.telegramService.impl;

import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.messengerEnums.Objects;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.models.messanger.Shell;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.example.demo.services.lvivCroissantRepositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageParserHelperService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.DONE;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.OWN_MENU_STATUS;

@Service
public class TelegramMessageParserHelperServiceImpl implements TelegramMessageParserHelperService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramCreatingOwnCroissantEventService telegramCreatingOwnCroissantEventService;

    private static final Logger logger = Logger.getLogger(TelegramMessageParserHelperServiceImpl.class);
    @Value("${subscription.url}")
    private String SUBSCRIPTION_URL;
    @Value("${app.verify.token}")
    private String VER_TOK;
    @Value("${server.url}")
    private String SERVER_URL;
    @Override
    public void helpStart(Message message) {
        TUser tUser = new TUser();
        tUser.setChatId(message.getChat().getId());
        tUser.setName(message.getFrom().getFirstName());
        tUser.setLastName(message.getFrom().getLastName());
        tUser.setLocale(message.getFrom().getLanguageCode());
        tUser.setRole(Roles.CUSTOMER);
        telegramUserRepositoryService.saveAndFlush(tUser);
    }

    @Override
    public void helpDeleteOrderings(Message message) {
        List<CustomerOrdering>customerOrderings = customerOrderingRepositoryService.findAll();
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        for(CustomerOrdering customerOrdering:customerOrderings){
          tUser.getCustomerOrderings().remove(customerOrdering);
          customerOrdering.setTUser(null);
          customerOrderingRepositoryService.delete(customerOrdering);
        }
        telegramUserRepositoryService.saveAndFlush(tUser);
        telegramMessageSenderService.simpleMessage( ResourceBundle.getBundle("dictionary").getString(DONE.name()),message);
    }

    @Override
    public void helpCreateOwnCroissant(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,OWN_MENU_STATUS);
        telegramCreatingOwnCroissantEventService.createOwn(message);

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
