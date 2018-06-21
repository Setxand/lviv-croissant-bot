package com.bots.lvivCroissantBot.controller.telegram;

import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.adminPanel.AdminPanelUpdateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminPanel")
public class TelegramAdminPanelWebhook {
    @Autowired
    private AdminPanelUpdateParser adminPanelUpdateParser;
    @PostMapping
    public void getUpdate(@RequestBody Update update){
        adminPanelUpdateParser.parseUpdate(update);
    }
}
