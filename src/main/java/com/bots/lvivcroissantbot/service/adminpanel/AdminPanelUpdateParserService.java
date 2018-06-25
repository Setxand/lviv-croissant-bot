package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.Update;

public interface AdminPanelUpdateParserService {
    public void parseUpdate(Update update);
}
