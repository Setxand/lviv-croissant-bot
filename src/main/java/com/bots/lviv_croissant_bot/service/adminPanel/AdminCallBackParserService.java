package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;

public interface AdminCallBackParserService {
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery);
}
