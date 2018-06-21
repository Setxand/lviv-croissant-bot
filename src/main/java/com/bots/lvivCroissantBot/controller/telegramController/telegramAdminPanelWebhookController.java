package com.bots.lvivCroissantBot.controller.telegramController;

import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.adminPanelService.AdminPanelUpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminPanel")
public class telegramAdminPanelWebhookController {
    @Autowired
    private AdminPanelUpdateParserService adminPanelUpdateParserService;
    @PostMapping
    public void getUpdate(@RequestBody Update update){
        adminPanelUpdateParserService.parseUpdate(update);
    }
}
