package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;

public interface AdminCallBackParserService {
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery);
}
