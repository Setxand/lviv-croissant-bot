package com.bots.lvivcroissantbot;

import com.bots.lvivcroissantbot.config.AppConfig;
import com.bots.lvivcroissantbot.config.client.UrlClient;
import com.bots.lvivcroissantbot.controller.TestController;
import com.bots.lvivcroissantbot.dto.messanger.*;
import com.bots.lvivcroissantbot.dto.telegram.Chat;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

import static com.bots.lvivcroissantbot.constantenum.messenger.payload.Payloads.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.ButtonType.postback;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.ButtonType.web_url;

@Component
public class ServerStarting {
    private final static Logger logger = LoggerFactory.getLogger(ServerStarting.class);
    @Autowired
    TestController testController;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private UrlClient urlClient;

    @Value("${messenger.page.access.token}")
    private String PAGE_ACCESS_TOKEN;

    private RestTemplate restTemplate;

    @PostConstruct
    public void getStarted() throws Exception {
        AppConfig.UrlProps urlProps = urlClient.getUrlProps();

        restTemplate = new RestTemplate();
        Shell shell = new Shell();
        shell.setWhiteListedDomains(new ArrayList<>(Arrays.asList(urlProps.getServer())));


        MessengerProfileApi messengerProfileApi = new MessengerProfileApi(new GetStarted(GET_STARTED_PAYLOAD.name()), new ArrayList<PersistentMenu>());
        PersistentMenu persistentMenu = new PersistentMenu();
        MenuItem menuItem = new MenuItem(web_url.name(), "Reference");
        menuItem.setUrl(urlProps.getServer() + "/reference");
        persistentMenu.setCallToActions(Arrays.asList(new MenuItem(postback.name(), "Menu of croissants", MENU_PAYLOAD.name())
                , new MenuItem(postback.name(), "Navigation menu", NAVIGATION_MENU.name())
                , menuItem));
        messengerProfileApi.getPersistentMenu().add(persistentMenu);


        try {
            messengerRequests(messengerProfileApi, urlProps, shell);

        } catch (Exception ex) {
            logger.error("Messenger error: " + ex);
        } finally {
            telegramRequests(urlProps);
        }
    }


    private void telegramRequests(AppConfig.UrlProps urlProps) {
        try {
            ResponseEntity<?> responseEntity = restTemplate.getForEntity(urlProps.getProfile().getTelegramCommon() + "/setWebhook?url=" + urlProps.getServer() + "/telegramWebHook", Object.class);
            logger.info("Telegram`s bot webhook: " + responseEntity.getBody().toString());
            ResponseEntity<?> adminPanelReg = restTemplate.getForEntity(urlProps.getProfile().getTelegramService() + "/setWebhook?url=" + urlProps.getServer() + "/adminpanel", Object.class);
            logger.info("Admin panel webhook: " + adminPanelReg.getBody().toString());

            Message message = new Message();
            message.setChat(new Chat(388073901));
            telegramMessageSenderService.simpleMessage("Server has ran", message);
        } catch (Exception e) {
            logger.error("Telegram error: " + e.getStackTrace().toString());
        }
    }

    private void messengerRequests(MessengerProfileApi messengerProfileApi, AppConfig.UrlProps urlProps, Shell shell) {
        testController.setObject(messengerProfileApi);
        String url = urlProps.getProfile().getMessenger() + PAGE_ACCESS_TOKEN;

        ResponseEntity<?> responseEntity = restTemplate
                .postForEntity(url, messengerProfileApi, MessengerProfileApi.class);
        logger.info("Messenger: persistence menu - " + responseEntity.toString());

        ResponseEntity<?> responseForWhiteList = restTemplate
                .postForEntity(urlProps.getProfile().getMessenger() + PAGE_ACCESS_TOKEN, shell, Shell.class);
        logger.info("Messenger: WhiteList domain - " + responseForWhiteList.toString());
    }


}


