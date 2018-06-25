package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;

public interface AdminCallBackParserService {
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery);
}
