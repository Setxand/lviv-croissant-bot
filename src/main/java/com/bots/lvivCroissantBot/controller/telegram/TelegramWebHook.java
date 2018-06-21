package com.bots.lvivCroissantBot.controller.telegram;

import com.bots.lvivCroissantBot.dto.telegram.Update;
import com.bots.lvivCroissantBot.service.telegram.UpdateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegramWebHook")
public class TelegramWebHook {
    @Autowired
    private UpdateParser updateParser;
    @PostMapping
    public void getUpdate(@RequestBody Update update){
        updateParser.parseUpdate(update);
    }
}
