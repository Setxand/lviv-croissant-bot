package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.Update;

public interface AdminPanelUpdateParser {
    public  void parseUpdate(Update update);
}
