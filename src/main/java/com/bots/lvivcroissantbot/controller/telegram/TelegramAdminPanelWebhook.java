package com.bots.lvivcroissantbot.controller.telegram;

import com.bots.lvivcroissantbot.dto.telegram.Update;
import com.bots.lvivcroissantbot.service.adminpanel.AdminPanelUpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminpanel")
public class TelegramAdminPanelWebhook {
    @Autowired
    private AdminPanelUpdateParserService adminPanelUpdateParserService;
    @PostMapping
    public void getUpdate(@RequestBody Update update){
        adminPanelUpdateParserService.parseUpdate(update);
    }
}
