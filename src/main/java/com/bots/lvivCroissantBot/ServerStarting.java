package com.bots.lvivCroissantBot;

import com.bots.lvivCroissantBot.dto.messanger.*;
import com.bots.lvivCroissantBot.dto.telegram.Chat;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
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

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.ButtonType.postback;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.ButtonType.web_url;

@Component
public class ServerStarting {
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Value("${page.access.token}")
    private String PAGE_ACCESS_TOKEN;

    @Value("${profile.api.uri}")
    private String FACEBOOK_PROFILE_URI;

    @Value("${server.url}")
    private String SERVER_URL;
    @Value("${telegran.url}")
    private String TELEGRAM_URL;
    @Value("${telegran.admins.url}")
    private String ADMIN_TELEGRAM_URL;

    private   final static Logger logger = LoggerFactory.getLogger(ServerStarting.class);
    private RestTemplate restTemplate;

    @PostConstruct
    public void getStarted() throws Exception {
        restTemplate = new RestTemplate();
        Shell shell = new Shell();
        shell.setWhiteListedDomains(new ArrayList<>(Arrays.asList(SERVER_URL)));


        MessengerProfileApi messengerProfileApi = new MessengerProfileApi(new GetStarted(GET_STARTED_PAYLOAD.name()), new ArrayList<PersistentMenu>());
        PersistentMenu persistentMenu = new PersistentMenu();
        MenuItem menuItem = new MenuItem(web_url.name(), "Reference");
        menuItem.setUrl(SERVER_URL + "/reference");
        persistentMenu.setCallToActions(Arrays.asList(new MenuItem(postback.name(), "Menu of croissants", MENU_PAYLOAD.name())
                , new MenuItem(postback.name(), "Navigation menu", NAVIGATION_MENU.name())
                , menuItem));
        messengerProfileApi.getPersistentMenu().add(persistentMenu);

        try {


            ResponseEntity<?> responseEntity = restTemplate
                    .postForEntity(FACEBOOK_PROFILE_URI + PAGE_ACCESS_TOKEN, messengerProfileApi, MessengerProfileApi.class);
            logger.info(responseEntity.toString());
            ResponseEntity<?> responseForWhiteList = restTemplate
                    .postForEntity(FACEBOOK_PROFILE_URI + PAGE_ACCESS_TOKEN, shell, Shell.class);
            logger.info(responseForWhiteList.toString());

        } catch (Exception ex) {
            logger.warn("Messenger queries: " + ex);
        } finally {
            try {
                ResponseEntity<?> responseEntity = restTemplate.getForEntity(TELEGRAM_URL + "/setWebhook?url=" + SERVER_URL + "/telegramWebHook", Object.class);
                logger.info("Telegram`s bot webhook: " + responseEntity.getBody().toString());

                ResponseEntity<?> adminPanelReg = restTemplate.getForEntity(ADMIN_TELEGRAM_URL + "/setWebhook?url=" + SERVER_URL + "/adminPanel", Object.class);
                logger.info("Admin panel webhook: " + adminPanelReg.getBody().toString());


                Message message = new Message();
                message.setChat(new Chat(388073901));
                telegramMessageSender.simpleMessage("Server has ran", message);
            } catch (Exception e) {
                logger.error(e.getStackTrace().toString());
            }
        }
    }


}


