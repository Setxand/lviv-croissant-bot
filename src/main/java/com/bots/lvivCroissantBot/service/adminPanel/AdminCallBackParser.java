package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;

public interface AdminCallBackParser {
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery);
}
