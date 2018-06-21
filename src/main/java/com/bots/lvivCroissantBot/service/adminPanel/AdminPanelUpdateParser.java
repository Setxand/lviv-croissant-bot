package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.Update;

public interface AdminPanelUpdateParser {
    public  void parseUpdate(Update update);
}
