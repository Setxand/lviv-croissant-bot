package com.bots.lviv_croissant_bot.service.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.Update;

public interface UpdateParserService {
    public void parseUpdate(Update update);
}
