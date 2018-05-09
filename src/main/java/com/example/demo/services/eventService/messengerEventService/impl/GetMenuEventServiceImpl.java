package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.entities.SupportEntity;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.models.messanger.*;
import com.example.demo.services.eventService.messengerEventService.GetMenuEventService;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.SupportEntityRepositoryService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.enums.messengerEnums.payloads.Payloads.*;
import static com.example.demo.enums.messengerEnums.payloads.QuickReplyPayloads.CROISSANT_TYPE_PAYLOAD;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.messengerEnums.types.AttachmentType.template;
import static com.example.demo.enums.messengerEnums.types.ButtonType.postback;
import static com.example.demo.enums.messengerEnums.types.ButtonType.web_url;
import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.OWN;

@Service
public class GetMenuEventServiceImpl implements GetMenuEventService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private UserRepositoryService userRepositoryService;
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
        List<Croissant> croissants;

        if (croissantType.equals(OWN.name())) {
            User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            List<Croissant> croissants1 = new ArrayList<>();
            if (user.getOwnCroissantsId() != null)
                for (Long id : user.getOwnCroissantsId()) {
                    if (croissantRepositoryService.findOne(id) != null)
                        croissants1.add(croissantRepositoryService.findOne(id));
                }
            Collections.reverse(croissants1);
            croissants = croissants1;
        } else
            croissants = croissantRepositoryService.findAllByType(croissantType);
        if (croissants.isEmpty())
            messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(), messaging.getSender().getId()), messaging.getSender().getId());
        else
            nonEmpTyListOfCroissants(messaging, croissants);


    }


    private void nonEmpTyListOfCroissants(Messaging messaging, List<Croissant> croissants) {
        Message message = new Message();
        Attachment attachment = new Attachment(template.name(), new Payload());
        message.setAttachment(attachment);
        List<Element> elements = new ArrayList<>();
        attachment.getPayload().setElements(fillingItems(elements, croissants, messaging.getSender().getId(), messaging));
        message.getAttachment().getPayload().setButtons(null);
        messageSenderService.sendMessage(new Messaging(message, new Recipient(messaging.getSender().getId())));
    }


    private List<Element> fillingItems(List<Element> elements, List<Croissant> croissants, Long recipient, Messaging messaging) {

        int index = 0;
        List<Croissant>croissantsSubList;
        if (messaging.getPostback() != null) {
            String payload = messaging.getPostback().getPayload();
            if (TextFormatter.ejectContext(payload).equals(FOR_GETTING_MENU.name())) {
                index= Integer.parseInt(TextFormatter.ejectVariableWithContext(messaging.getPostback().getPayload()));
            }

        }
      try {
                croissantsSubList = croissants.subList(index,index+10);
                index+=9;
            }
            catch (Exception ex){
                croissantsSubList = croissants.subList(index,croissants.size());
                index+=(croissants.size()-index-1);

            }

        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
        for (Croissant croissant:croissantsSubList) {
            Element element = new Element();

            if(croissant == croissantsSubList.get(croissantsSubList.size()-1) && croissant !=croissants.get(croissants.size()-1)) {
                addShowMore(index, element, elements,messaging);
                break;
            }
            element = elementInit(element, croissants, messaging,croissant);
            if(croissant.getType().equals(OWN.name()) || user.getRole() != Roles.CUSTOMER)
                addDeleteButton(element, messaging,croissant);

            elements.add(element);
        }



        return elements;
    }

    private void addDeleteButton(Element element,  Messaging messaging, Croissant croissant) {

        Button button1 = new Button(postback.name(), recognizeService.recognize(DELETE_BUTTON.name(), messaging.getSender().getId()));
        button1.setPayload(DELETE_BUTTON_PAYLOAD.name() + "?" + croissant.getId().toString());
        element.getButtons().add(button1);
    }



    private Element elementInit(Element element, List<Croissant> croissants, Messaging messaging, Croissant croissant) {
        element.setImage_url(croissant.getImageUrl());
        element.setTitle(croissant.getName() + ", " + recognizeService.recognize(PRICE.name(), messaging.getSender().getId()) + ": " + croissant.getPrice() + recognizeService.recognize(CURRENCY.name(), messaging.getSender().getId()));
        Button button = new Button(postback.name(), recognizeService.recognize(MAKE_ORDER.name(), messaging.getSender().getId()));
        element.setSubtitle(counter(croissant.getCroissantsFillings()));
        button.setPayload(ORDER_PAYLOAD.name() + "?" + croissant.getId().toString());
        Button viewButton = new Button(web_url.name(),recognizeService.recognize(MORE_INFO.name(),messaging.getSender().getId()));
        viewButton.setMesExtentions(true);
        viewButton.setUrl(SERVER_URL+"/fillings/"+croissant.getId());
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


    private String counter(List<CroissantsFilling> croissantsFillings) {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(croissantsFillings.toString());
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



















