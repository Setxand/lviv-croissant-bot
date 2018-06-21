package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.Update;

public interface AdminPanelUpdateParserService {
    public  void parseUpdate(Update update);
}
