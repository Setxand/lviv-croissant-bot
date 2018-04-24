package com.example.demo;

import com.example.demo.models.messanger.*;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.demo.enums.messengerEnums.payloads.Payloads.*;
import static com.example.demo.enums.messengerEnums.types.ButtonType.postback;
import static com.example.demo.enums.messengerEnums.types.ButtonType.web_url;

@Component
public class ServerStarting {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Value("${page.access.token}")
    private String PAGE_ACCESS_TOKEN;

    @Value("${profile.api.uri}")
    private String FACEBOOK_PROFILE_URI;

    @Value("${server.url}")
    private String SERVER_URL;
    @Value("${telegran.url}")
    private String TELEGRAM_URL;


    private static final Logger logger = Logger.getLogger(ServerStarting.class);

    @PostConstruct
    public void getStarted() throws Exception {
        Shell shell = new Shell();
        shell.setWhiteListedDomains(new ArrayList<>(Arrays.asList(SERVER_URL)));



        MessengerProfileApi messengerProfileApi = new MessengerProfileApi(new GetStarted(GET_STARTED_PAYLOAD.name()), new ArrayList<PersistentMenu>());
        PersistentMenu persistentMenu = new PersistentMenu();
        MenuItem menuItem = new MenuItem(web_url.name(),"Reference");
        menuItem.setUrl(SERVER_URL+"/reference");
        persistentMenu.setCallToActions(Arrays.asList(new MenuItem(postback.name(), "Menu of croissants", MENU_PAYLOAD.name())
        ,new MenuItem(postback.name(),"Navigation menu",NAVIGATION_MENU.name())
        ,menuItem));
        messengerProfileApi.getPersistentMenu().add(persistentMenu);

        try {


            ResponseEntity<?> responseEntity = new RestTemplate()
                    .postForEntity(FACEBOOK_PROFILE_URI + PAGE_ACCESS_TOKEN, messengerProfileApi, MessengerProfileApi.class);
            logger.debug(responseEntity);
            ResponseEntity<?> responseForWhiteList = new RestTemplate()
                    .postForEntity(FACEBOOK_PROFILE_URI + PAGE_ACCESS_TOKEN, shell, Shell.class);
            logger.debug(responseForWhiteList);

        } catch (Exception ex) {
            logger.warn("Messenger queries: "+ex);
        }
        finally {
            try {
                ResponseEntity<?>responseEntity = new RestTemplate().getForEntity(TELEGRAM_URL+"/setWebhook?url="+SERVER_URL+"/telegramWebHook",Object.class);
                logger.debug("Telegram webhook: "+responseEntity.getBody().toString());
                telegramMessageSenderService.simpleMessage(388073901,"Server has ran");
            }
            catch (Exception e){
                logger.warn(e);
            }
        }
    }


}


