package com.bots.lvivcroissantbot.service.telegram;

import com.bots.lvivcroissantbot.dto.telegram.Update;

public interface UpdateParserService {
    public void parseUpdate(Update update);
}
