package com.example.demo.service.telegramService.impl;

import com.example.demo.constantEnum.Status;
import com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker;
import com.example.demo.entity.SpeakingMessage;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.dto.telegram.Message;
import com.example.demo.entity.peopleRegister.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.repositoryService.SpeakingMessagesRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.TextFormatter;
import com.example.demo.service.telegramService.TelegramMessageParserHelperService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.print.ServiceDialog;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.QUESTION_APPROVING;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constantEnum.telegramEnums.CallBackData.QUESTION_HAVING_MESSENGER_DATA;
import static com.example.demo.constantEnum.telegramEnums.TelegramUserStatus.OWN_MENU_STATUS;
import static sun.print.ServiceDialog.APPROVE;

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
    @Autowired
    private SpeakingMessagesRepositoryService speakingMessagesRepositoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    private static final Logger logger = Logger.getLogger(TelegramMessageParserHelperServiceImpl.class);
    @Value("${subscription.url}")
    private String SUBSCRIPTION_URL;
    @Value("${app.verify.token}")
    private String VER_TOK;
    @Value("${server.url}")
    private String SERVER_URL;
    @Override
    public void helpStart(Message message) {

        Optional<TUser>tUserOptional = Optional.ofNullable(telegramUserRepositoryService.findByChatId(message.getChat().getId()));

        if(tUserOptional.isPresent()) {

            Optional<MUser> mUserOptional = Optional.ofNullable(tUserOptional.get().getUser().getMUser());
            if (!mUserOptional.isPresent()) {


//                tUser.setLocale(message.getFrom().getLanguageCode());
//                tUser.setRole(Role.CUSTOMER);
//
//                tUser.setName(message.getFrom().getFirstName());
//                tUser.setLastName(message.getFrom().getLastName());
//                tUser.setChatId(message.getChat().getId());
//                tUser.setUserName(message.getFrom().getUserName());


//                telegramUserRepositoryService.saveAndFlush(tUser);

            }
        }
        else userSettings(message);
        SpeakingMessage speakingMessage = speakingMessagesRepositoryService.findByKey(HELLO_MESSAGE.name());
        if(message.getPlatform()==null)
        telegramMessageSenderService.simpleMessage(speakingMessage.getMessage(),message);
        else{
            String helloMessage = ResourceBundle.getBundle("dictionary").getString(HELLO_SERVICE.name());
            telegramMessageSenderService.simpleMessage(helloMessage,message);
        }
        telegramMessageSenderService.simpleQuestion(QUESTION_HAVING_MESSENGER_DATA,"?",ResourceBundle.getBundle("dictionary").getString(HAVING_MESSENGER.name()),message);


    }

    private void userSettings(Message message) {
        TUser tUser = new TUser();
        tUser.setLocale(message.getFrom().getLanguageCode());
        tUser.setName(message.getFrom().getFirstName());
        tUser.setLastName(message.getFrom().getLastName());
        tUser.setChatId(message.getChat().getId());
        tUser.setUserName(message.getFrom().getUserName());
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
    public void helpEnterPhoneInStart(Message message) {
        if(TextFormatter.isPhoneNumber(message.getText())){
            checkingNumber(message);
        }
        else {
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name()),message);
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),message);
        }
    }

    private void checkingNumber(Message message) {
        TUser tUser= telegramUserRepositoryService.findByChatId(message.getChat().getId());
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByPhoneNumber(message.getText()));
        if(userOptional.isPresent()){
            ifUserPresent(userOptional,tUser,message);
        }
        else
            userCreating(message,tUser);
        telegramUserRepositoryService.changeStatus(tUser,null);
    }

    private void ifUserPresent(Optional<User> userOptional, TUser tUser, Message message) {
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(FOUND_MESSENGER_USER.name()), message);
        MUser mUser = userOptional.get().getMUser();
        messageSenderService.sendSimpleQuestion(mUser.getRecipientId(), recognizeService.recognize(ServerSideSpeaker.APPROVE.name(), mUser.getRecipientId()), QUESTION_APPROVING.name() + "?" + message.getChat().getId(), "&");
    }

    private void userCreating(Message message, TUser tUser) {
        User user = new User();
        user.setTUser(tUser);
        user.setRole(Role.CUSTOMER);
        user.setStatus(Status.ACTIVE);
        userRepository.saveAndFlush(user);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NEW_USER.name()),message);
        telegramMessageSenderService.sendActions(message);
    }


}
