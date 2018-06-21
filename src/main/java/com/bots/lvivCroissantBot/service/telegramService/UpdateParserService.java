package com.bots.lvivCroissantBot.service.telegramService;

import com.bots.lvivCroissantBot.dto.telegram.Update;

public interface UpdateParserService {
    public void parseUpdate(Update update);
}
