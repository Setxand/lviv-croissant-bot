package com.bots.lvivCroissantBot.service.telegram;

import com.bots.lvivCroissantBot.dto.telegram.Update;

public interface UpdateParser {
    public void parseUpdate(Update update);
}
