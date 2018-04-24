package com.example.demo.services.messangerService.impl;

import com.example.demo.controllers.TestController;
import com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker;
import com.example.demo.enums.messengerEnums.types.ContentType;
import com.example.demo.enums.messengerEnums.types.CroissantsTypes;
import com.example.demo.models.messanger.*;
import com.example.demo.models.messanger.Requests.RequestByMessage;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantsFillingRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.enums.messengerEnums.CasesCourierActions.COMPLETING_ORDERINGS;
import static com.example.demo.enums.messengerEnums.CasesCourierActions.GET_LIST_OF_ORDERING;
import static com.example.demo.enums.messengerEnums.PayloadCases.*;
import static com.example.demo.enums.messengerEnums.payloads.Payloads.CREATE_OWN_CROISSANT_PAYLOAD;
import static com.example.demo.enums.messengerEnums.payloads.Payloads.MENU_PAYLOAD;
import static com.example.demo.enums.messengerEnums.payloads.QuickReplyPayloads.CROISSANT_TYPE_PAYLOAD;
import static com.example.demo.enums.messengerEnums.payloads.QuickReplyPayloads.LANGUAGE_PAYLOAD;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.messengerEnums.types.AttachmentType.template;
import static com.example.demo.enums.messengerEnums.types.ButtonType.postback;
import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.SANDWICH;
import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.SWEET;
import static com.example.demo.enums.messengerEnums.types.TemplateType.button;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private CroissantsFillingRepositoryService croissantsFillingRepositoryService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private RecognizeService recognizeService;

    @Autowired
    UserRepositoryService userRepositoryService;

    @Value("${page.access.token}")
    private String PAGE_ACCESS_TOKEN;

    @Value("${send.api.uri}")
    private String FACEBOOK_SEND_URL;

    @Value("${facebook.user.data.url}")
    private String USER_DATA_URL;

    @Value("${facebook.user.data.uer.fields}")
    private String DATA_FIELDS;

    @Value("${server.url}")
    private String SERVER_URL;

    private static final Logger logger = Logger.getLogger(MessageSenderServiceImpl.class);


    @Override
    public void errorMessage(Long recipient) {
        Message message = new Message(recognizeService.recognize(ERROR_MESSAGE.name(),recipient));
        Messaging messaging = new Messaging(message, new Recipient(recipient));
        sendMessage(messaging);
    }

    @Override
    public void sendSimpleMessage(String text, Long recipient) {
        Message message = new Message(text);
        Messaging messaging = new Messaging(message,new Recipient(recipient));

        sendMessage(messaging);
    }

    @Override
    public void askForCourierActions(Long recipient) {
        List<QuickReply>quickReplies = new ArrayList<>();
        quickReplies.add(new QuickReply(ContentType.text.name(),recognizeService.recognize(ORDERING_LIST.name(),recipient), GET_LIST_OF_ORDERING.name()));
        quickReplies.add(new QuickReply(ContentType.text.name(),recognizeService.recognize(COMPLETE_ORDERING.name(),recipient), COMPLETING_ORDERINGS.name()));
        Message message = new Message(recognizeService.recognize(CHOOSE_ACTIONS.name(),recipient));
        message.setQuickReplies(quickReplies);
        Messaging messaging1 = new Messaging(message,new Recipient(recipient));
        sendMessage(messaging1);
    }

    @Override
    public void sendSimpleQuestion(Long recipient,String text, String payload,String splitter) {
        List<QuickReply> quickReplies = Arrays.asList(new QuickReply(ContentType.text.name(),recognizeService.recognize(ServerSideSpeaker.YES.name(),recipient),payload+splitter+QUESTION_YES.name())
                , new QuickReply(ContentType.text.name(),recognizeService.recognize(NO.name(),recipient),payload+splitter+QUESTION_NO.name()));
        Message message = new Message(text);
        message.setQuickReplies(quickReplies);
        Messaging messaging1 = new Messaging(message,new Recipient(recipient));
        sendMessage(messaging1);
    }

    @Override
    public void askTypeOfCroissants(Long recipient, String payload ) {
        Message message = new Message(recognizeService.recognize(CHOOSE_TYPE_CROISSANT.name(),recipient));
        List<QuickReply>quickReplies = new ArrayList<>();
        quickReplies.add(new QuickReply(ContentType.text.name(),recognizeService.recognize(ServerSideSpeaker.SWEET.name(),recipient),payload+SWEET.name()));
        quickReplies.add(new QuickReply(ContentType.text.name(),recognizeService.recognize(ServerSideSpeaker.SANDWICH.name(),recipient),payload+SANDWICH.name()));
        if(payload.equals(CROISSANT_TYPE_PAYLOAD.name()+"?")){
            quickReplies.add(new QuickReply(ContentType.text.name(),recognizeService.recognize(ServerSideSpeaker.OWN.name(),recipient),payload+ CroissantsTypes.OWN.name()));
        }

        message.setQuickReplies(quickReplies);
        sendMessage(new Messaging(message,new Recipient(recipient)));
    }

    @Override
    public void askCroissantName(Messaging messaging) {
        Message message = new Message(recognizeService.recognize(NAMING_CROISSANT.name(),messaging.getSender().getId()));
        sendMessage(new Messaging(message,new Recipient(messaging.getSender().getId())));
    }

    @Override
    public void askSelectLanguage(Long recipient) {
        List<QuickReply>quickReplies = Arrays.asList(new QuickReply(ContentType.text.name(),"Українська", LANGUAGE_PAYLOAD.name()+"?"+ UA.name())
                ,new QuickReply(ContentType.text.name(),"English",LANGUAGE_PAYLOAD.name()+"?"+EN.name()));
        Message message = new Message("Select language:");
        message.setQuickReplies(quickReplies);
        sendMessage(new Messaging(message,new Recipient(recipient)));
    }

    @Override
    public UserData sendFacebookRequest(Long recipient) {
        ResponseEntity<UserData> response = new RestTemplate().getForEntity(USER_DATA_URL+recipient+DATA_FIELDS+PAGE_ACCESS_TOKEN, UserData.class);
        UserData userData = response.getBody();
        logger.debug("The message for Facebook was sent successfully"+response);
        return userData;
    }

    @Override
    public void sendUserActions(Long recipient ) {
        Attachment attachment = new Attachment();
        attachment.setType(template.name());
            Payload payload = new Payload();
            attachment.setPayload(payload);
            payload.setTemplateType(button.name());
            payload.setText(recognizeService.recognize(CHOOSE_ACtIONS.name(),recipient));
            payload.setButtons(new ArrayList<Button>(Arrays.asList(new Button(postback.name(),recognizeService.recognize(ServerSideSpeaker.MENU_OF_CROISSANTS.name(),recipient),MENU_PAYLOAD.name())
                    ,new Button(postback.name(),recognizeService.recognize(ServerSideSpeaker.CREATE_OWN_CROISSANT.name(),recipient), CREATE_OWN_CROISSANT_PAYLOAD.name()))));
            payload.setElements(null);
            Message message = new Message();
            message.setAttachment(attachment);

            sendMessage(new Messaging(message,new Recipient(recipient)));
    }

    @Override
    public void sendButtons(List<Button> buttons, String text, Long recipient) {
        Attachment attachment = new Attachment();
        attachment.setType(template.name());
        Payload payload = new Payload();
        payload.setTemplateType(button.name());
        payload.setButtons(buttons);
        payload.setText(text);
        attachment.setPayload(payload);
        Message message = new Message();
        message.setAttachment(attachment);
        sendMessage(new Messaging(message,new Recipient(recipient)));
    }

    @Override
    public void sendQuickReplies(List<QuickReply> quickReplies,String text, Long recipient) {
        Message message = new Message();
        message.setQuickReplies(quickReplies);
        message.setText(text);
        sendMessage(new Messaging(message,new Recipient(recipient)));
    }

@Autowired
    TestController testController;
    @Override
        public @ResponseBody void sendMessage(Messaging messaging) {
        testController.setObject(messaging);
        new RestTemplate().postForEntity(FACEBOOK_SEND_URL+PAGE_ACCESS_TOKEN, messaging, Void.class);

}


}
