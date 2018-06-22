package com.bots.lviv_croissant_bot.controller.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.Update;
import com.bots.lviv_croissant_bot.service.telegram.UpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegramWebHook")
public class TelegramWebHook {
    @Autowired
    private UpdateParserService updateParserService;
    @PostMapping
    public void getUpdate(@RequestBody Update update){
        updateParserService.parseUpdate(update);
    }
}
