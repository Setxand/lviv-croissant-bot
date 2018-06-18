package com.example.demo.service.eventService.messengerEventService.impl;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.example.demo.entity.SupportEntity;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.dto.messanger.*;
import com.example.demo.service.eventService.messengerEventService.GetMenuEventService;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import com.example.demo.service.repositoryService.SupportEntityRepositoryService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.peopleRegisterService.MUserRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.constantEnum.messengerEnums.payloads.Payloads.*;
import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.CROISSANT_TYPE_PAYLOAD;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constantEnum.messengerEnums.types.AttachmentType.template;
import static com.example.demo.constantEnum.messengerEnums.types.ButtonType.postback;
import static com.example.demo.constantEnum.messengerEnums.types.ButtonType.web_url;
import static com.example.demo.constantEnum.messengerEnums.types.CroissantsTypes.OWN;

@Service
public class GetMenuEventServiceImpl implements GetMenuEventService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;


    @Value("${server.url}")
    private String SERVER_URL;
    @Override
    public void getMenu(Messaging messaging) {
        if (messaging.getPostback() != null) {
            String payload = messaging.getPostback().getPayload();
                if (TextFormatter.ejectContext(payload).equals(FOR_GETTING_MENU.name()) || TextFormatter.ejectPaySinglePayload(payload).equals(MENU_PAYLOAD.name())) {
                    initAndSendQueryAllCr(messaging);
            }
        } else if (messaging.getMessage().getQuickReply() == null) {
            messageSenderService.askTypeOfCroissants(messaging.getSender().getId(), CROISSANT_TYPE_PAYLOAD.name() + "?");
        } else
            initAndSendQueryAllCr(messaging);

    }


    private void initAndSendQueryAllCr(Messaging messaging) {
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        String croissantType = supportEntity.getType();
        List<CroissantEntity> croissantEntities;

        if (croissantType.equals(OWN.name())) {
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            List<CroissantEntity> croissants1 = new ArrayList<>();
            if (MUser.getOwnCroissantsId() != null)
                for (Long id : MUser.getOwnCroissantsId()) {
                    if (croissantRepositoryService.findOne(id) != null)
                        croissants1.add(croissantRepositoryService.findOne(id));
                }
            Collections.reverse(croissants1);
            croissantEntities = croissants1;
        } else
            croissantEntities = croissantRepositoryService.findAllByType(croissantType);
        if (croissantEntities.isEmpty())
            messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(), messaging.getSender().getId()), messaging.getSender().getId());
        else
            nonEmpTyListOfCroissants(messaging, croissantEntities);


    }


    private void nonEmpTyListOfCroissants(Messaging messaging, List<CroissantEntity> croissantEntities) {
        Message message = new Message();
        Attachment attachment = new Attachment(template.name(), new Payload());
        message.setAttachment(attachment);
        List<Element> elements = new ArrayList<>();
        attachment.getPayload().setElements(fillingItems(elements, croissantEntities, messaging.getSender().getId(), messaging));
        message.getAttachment().getPayload().setButtons(null);
        messageSenderService.sendMessage(new Messaging(message, new Recipient(messaging.getSender().getId())));
    }


    private List<Element> fillingItems(List<Element> elements, List<CroissantEntity> croissantEntities, Long recipient, Messaging messaging) {

        int index = 0;
        List<CroissantEntity>croissantsSubList;
        if (messaging.getPostback() != null) {
            String payload = messaging.getPostback().getPayload();
            if (TextFormatter.ejectContext(payload).equals(FOR_GETTING_MENU.name())) {
                index= Integer.parseInt(TextFormatter.ejectVariableWithContext(messaging.getPostback().getPayload()));
            }

        }
      try {
                croissantsSubList = croissantEntities.subList(index,index+10);
                index+=9;
            }
            catch (Exception ex){
                croissantsSubList = croissantEntities.subList(index, croissantEntities.size());
                index+=(croissantEntities.size()-index-1);

            }

        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        for (CroissantEntity croissantEntity :croissantsSubList) {
            Element element = new Element();

            if(croissantEntity == croissantsSubList.get(croissantsSubList.size()-1) && croissantEntity != croissantEntities.get(croissantEntities.size()-1)) {
                addShowMore(index, element, elements,messaging);
                break;
            }
            element = elementInit(element, croissantEntities, messaging, croissantEntity);
            if(croissantEntity.getType().equals(OWN.name()) || MUser.getUser().getRole() != Role.CUSTOMER)
                addDeleteButton(element, messaging, croissantEntity);

            elements.add(element);
        }



        return elements;
    }

    private void addDeleteButton(Element element,  Messaging messaging, CroissantEntity croissantEntity) {

        Button button1 = new Button(postback.name(), recognizeService.recognize(DELETE_BUTTON.name(), messaging.getSender().getId()));
        button1.setPayload(DELETE_BUTTON_PAYLOAD.name() + "?" + croissantEntity.getId().toString());
        element.getButtons().add(button1);
    }



    private Element elementInit(Element element, List<CroissantEntity> croissantEntities, Messaging messaging, CroissantEntity croissantEntity) {
        element.setImage_url(croissantEntity.getImageUrl());
        element.setTitle(croissantEntity.getName() + ", " + recognizeService.recognize(PRICE.name(), messaging.getSender().getId()) + ": " + croissantEntity.getPrice() + recognizeService.recognize(CURRENCY.name(), messaging.getSender().getId()));
        Button button = new Button(postback.name(), recognizeService.recognize(MAKE_ORDER.name(), messaging.getSender().getId()));
        element.setSubtitle(counter(croissantEntity.getCroissantsFillings()));
        button.setPayload(ORDER_PAYLOAD.name() + "?" + croissantEntity.getId().toString());
        Button viewButton = new Button(web_url.name(),recognizeService.recognize(MORE_INFO.name(),messaging.getSender().getId()));
        viewButton.setMesExtentions(true);
        viewButton.setUrl(SERVER_URL+"/fillings/"+ croissantEntity.getId());
        viewButton.setHeightRatio("tall");
        element.getButtons().add(button);
        element.getButtons().add(viewButton);
        return element;
    }

    private void addShowMore(int index, Element element, List<Element> elements, Messaging messaging) {
        Button button = new Button(postback.name(), recognizeService.recognize(SHOW_MORE_BUTTON.name(),messaging.getSender().getId()), SHOW_MORE_PAYLOAD.name() + "?" + FOR_GETTING_MENU.name() + "&" + index);
        element.getButtons().add(button);
        element.setTitle(recognizeService.recognize(NOT_ALL_CROISSANTS.name(),messaging.getSender().getId()));
        elements.add(element);

    }


    private String counter(List<CroissantsFilling> croissantsFillingEntities) {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(croissantsFillingEntities.toString());
        stringBuilder.deleteCharAt(0);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        List<String> strings = Arrays.asList(stringBuilder.toString().trim().split(","));
        Set<String> stringSet = new HashSet<>(strings);
        for (String s : stringSet) {
            int occurrences = Collections.frequency(strings, s);
            result += " " + s + " x" + occurrences + " ";
        }

        return result;
    }
}



















