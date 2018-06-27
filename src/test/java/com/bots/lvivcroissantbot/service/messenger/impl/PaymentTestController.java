package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.DemoApplicationTests;
import com.bots.lvivcroissantbot.dto.messanger.Button;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.bots.lvivcroissantbot.constantenum.messenger.type.ButtonType.web_url;

public class PaymentTestController extends DemoApplicationTests {
    List<Button> buttons;
    @Value("${url.server}")
    private String SERVER_URL;
    @Mock
    RestTemplate restTemplate;
    @Autowired
    MessageSenderService messageSenderService;

    @Before
    public void setUp() {
        buttons = new ArrayList<>();
        Button button = new Button(web_url.name(), "payment");
        button.setMesExtentions(true);
        button.setUrl(SERVER_URL + "/payment");
        buttons.add(button);

    }


    @Test
    public void paymentTesting() {
        messageSenderService.sendButtons(buttons, "payment", userId);
        logger.info("payment button has bet sent");
        HttpHeaders httpHeaders = new HttpHeaders();


    }
}
