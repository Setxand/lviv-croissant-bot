package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker;
import com.bots.lvivcroissantbot.constantenum.messenger.type.ContentType;
import com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes;
import com.bots.lvivcroissantbot.dto.messanger.*;
import com.bots.lvivcroissantbot.repository.CroisantsFillingEntityRepository;
import com.bots.lvivcroissantbot.repository.MenuOfFillingRepository;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bots.lvivcroissantbot.constantenum.messenger.CasesCourierActions.COMPLETING_ORDERINGS;
import static com.bots.lvivcroissantbot.constantenum.messenger.CasesCourierActions.GET_LIST_OF_ORDERING;
import static com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.Payloads.CREATE_OWN_CROISSANT_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.Payloads.MENU_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.CROISSANT_TYPE_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.LANGUAGE_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.AttachmentType.template;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.ButtonType.postback;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes.SANDWICH;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes.SWEET;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.TemplateType.button;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    private final static Logger logger = LoggerFactory.getLogger(MessageSenderServiceImpl.class);
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private CroisantsFillingEntityRepository croisantsFillingEntityRepository;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Autowired
    private RecognizeService recognizeService;
    @Value("${messenger.page.access.token}")
    private String PAGE_ACCESS_TOKEN;
    @Value("${messenger.send.api.uri}")
    private String FACEBOOK_SEND_URL;
    @Value("${messenger.facebook.MUser.data.url}")
    private String USER_DATA_URL;
    @Value("${messenger.facebook.MUser.data.uer.fields}")
    private String DATA_FIELDS;

    private RestTemplate restTemplate;


    @Override
    public void errorMessage(Long recipient) {
        Message message = new Message(recognizeService.recognize(ERROR_MESSAGE.name(), recipient));
        Messaging messaging = new Messaging(message, new Recipient(recipient));
        sendMessage(messaging);
    }

    @Override
    public void sendSimpleMessage(String text, Long recipient) {
        Message message = new Message(text);
        Messaging messaging = new Messaging(message, new Recipient(recipient));

        sendMessage(messaging);
    }

    @Override
    public void askForCourierActions(Long recipient) {
        List<QuickReply> quickReplies = new ArrayList<>();
        quickReplies.add(new QuickReply(ContentType.text.name(), recognizeService.recognize(ORDERING_LIST.name(), recipient), GET_LIST_OF_ORDERING.name()));
        quickReplies.add(new QuickReply(ContentType.text.name(), recognizeService.recognize(COMPLETE_ORDERING.name(), recipient), COMPLETING_ORDERINGS.name()));
        Message message = new Message(recognizeService.recognize(CHOOSE_ACTIONS.name(), recipient));
        message.setQuickReplies(quickReplies);
        Messaging messaging1 = new Messaging(message, new Recipient(recipient));
        sendMessage(messaging1);
    }

    @Override
    public void sendSimpleQuestion(Long recipient, String text, String payload, String splitter) {
        List<QuickReply> quickReplies = Arrays.asList(new QuickReply(ContentType.text.name(), recognizeService.recognize(ServerSideSpeaker.YES.name(), recipient), payload + splitter + QUESTION_YES.name())
                , new QuickReply(ContentType.text.name(), recognizeService.recognize(NO.name(), recipient), payload + splitter + QUESTION_NO.name()));
        Message message = new Message(text);
        message.setQuickReplies(quickReplies);
        Messaging messaging1 = new Messaging(message, new Recipient(recipient));
        sendMessage(messaging1);
    }

    @Override
    public void askTypeOfCroissants(Long recipient, String payload) {
        Message message = new Message(recognizeService.recognize(CHOOSE_TYPE_CROISSANT.name(), recipient));
        List<QuickReply> quickReplies = new ArrayList<>();
        quickReplies.add(new QuickReply(ContentType.text.name(), recognizeService.recognize(ServerSideSpeaker.SWEET.name(), recipient), payload + SWEET.name()));
        quickReplies.add(new QuickReply(ContentType.text.name(), recognizeService.recognize(ServerSideSpeaker.SANDWICH.name(), recipient), payload + SANDWICH.name()));
        if (payload.equals(CROISSANT_TYPE_PAYLOAD.name() + "?")) {
            quickReplies.add(new QuickReply(ContentType.text.name(), recognizeService.recognize(ServerSideSpeaker.OWN.name(), recipient), payload + CroissantsTypes.OWN.name()));
        }

        message.setQuickReplies(quickReplies);
        sendMessage(new Messaging(message, new Recipient(recipient)));
    }

    @Override
    public void askCroissantName(Messaging messaging) {
        Message message = new Message(recognizeService.recognize(NAMING_CROISSANT.name(), messaging.getSender().getId()));
        sendMessage(new Messaging(message, new Recipient(messaging.getSender().getId())));
    }

    @Override
    public void askSelectLanguage(Long recipient) {
        List<QuickReply> quickReplies = Arrays.asList(new QuickReply(ContentType.text.name(), "Українська", LANGUAGE_PAYLOAD.name() + "?" + UA.name())
                , new QuickReply(ContentType.text.name(), "English", LANGUAGE_PAYLOAD.name() + "?" + EN.name()));
        Message message = new Message("Select language:");
        message.setQuickReplies(quickReplies);
        sendMessage(new Messaging(message, new Recipient(recipient)));
    }

    @Override
    public UserData sendFacebookRequest(Long recipient) {
        ResponseEntity<UserData> response = new RestTemplate().getForEntity(USER_DATA_URL + recipient + DATA_FIELDS + PAGE_ACCESS_TOKEN, UserData.class);
        UserData userData = response.getBody();
        logger.debug("The message for Facebook was sent successfully" + response);
        return userData;
    }

    @Override
    public void sendUserActions(Long recipient) {
        Attachment attachment = new Attachment();
        attachment.setType(template.name());
        Payload payload = new Payload();
        attachment.setPayload(payload);
        payload.setTemplateType(button.name());
        payload.setText(recognizeService.recognize(CHOOSE_ACtIONS.name(), recipient));
        payload.setButtons(new ArrayList<Button>(Arrays.asList(new Button(postback.name(), recognizeService.recognize(ServerSideSpeaker.MENU_OF_CROISSANTS.name(), recipient), MENU_PAYLOAD.name())
                , new Button(postback.name(), recognizeService.recognize(ServerSideSpeaker.CREATE_OWN_CROISSANT.name(), recipient), CREATE_OWN_CROISSANT_PAYLOAD.name()))));
        payload.setElements(null);
        Message message = new Message();
        message.setAttachment(attachment);

        sendMessage(new Messaging(message, new Recipient(recipient)));
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
        sendMessage(new Messaging(message, new Recipient(recipient)));

//        restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set(HttpHeaders.AUTHORIZATION,);
//
//        HttpEntity<Messaging>httpEntity = new HttpEntity<>(new Messaging(message,new Recipient(recipient)),httpHeaders);


    }

    @Override
    public void sendQuickReplies(List<QuickReply> quickReplies, String text, Long recipient) {
        Message message = new Message();
        message.setQuickReplies(quickReplies);
        message.setText(text);
        sendMessage(new Messaging(message, new Recipient(recipient)));
    }

    @Override
    public @ResponseBody
    void sendMessage(Messaging messaging) {

        new RestTemplate().postForEntity(FACEBOOK_SEND_URL + PAGE_ACCESS_TOKEN, messaging, Void.class);

    }


}
