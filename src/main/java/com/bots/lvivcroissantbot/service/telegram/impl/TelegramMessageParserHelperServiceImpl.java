package com.bots.lvivcroissantbot.service.telegram.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker;
import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;
import com.bots.lvivcroissantbot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivcroissantbot.entity.SpeakingMessage;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.entity.register.User;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.SpeakingMessagesRepository;
import com.bots.lvivcroissantbot.repository.UserRepository;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramCreatingOwnCroissantService;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageParserHelperService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.QUESTION_APPROVING;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.Buttons.CANCEL_INLINE_BUTTON;
import static com.bots.lvivcroissantbot.constantenum.telegram.Buttons.REINPUT_INLINE_BUTTON;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.CANCEL_INPUT_NUMBER_DATA;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.QUESTION_HAVING_MESSENGER_DATA;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.RERINPUT_NUMBER_DATA;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.OWN_MENU_STATUS;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.PHONE_ENTERING_IN_START_STATUS;

@Service
public class TelegramMessageParserHelperServiceImpl implements TelegramMessageParserHelperService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramCreatingOwnCroissantService telegramCreatingOwnCroissant;
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepositoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    final static Logger logger = LoggerFactory.getLogger(TelegramMessageParserHelperServiceImpl.class);
    @Value("${messenger.subscription.url}")
    private String SUBSCRIPTION_URL;
    @Value("${messenger.app.verify.token}")
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
        SpeakingMessage speakingMessage = speakingMessagesRepositoryService.findById(HELLO_MESSAGE.name()).orElse(new SpeakingMessage(HELLO_MESSAGE.name(),"Hi!"));
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
        telegramCreatingOwnCroissant.createOwn(message);

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

    @Override
    public void helpReinputData(CallBackQuery callBackQuery) {
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),callBackQuery.getMessage());
        telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId()),PHONE_ENTERING_IN_START_STATUS);
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
        List<InlineKeyboardButton>inlineKeyboardButtons = Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("buttons").getString(CANCEL_INLINE_BUTTON.name()),CANCEL_INPUT_NUMBER_DATA.name()+"?"+tUser.getChatId()),
                new InlineKeyboardButton(ResourceBundle.getBundle("buttons").getString(REINPUT_INLINE_BUTTON.name()), RERINPUT_NUMBER_DATA.name()));
        telegramMessageSenderService.sendInlineButtons(Arrays.asList(inlineKeyboardButtons),ResourceBundle.getBundle("dictionary").getString(NO_SUCH_USERS.name()),message);

    }


}
