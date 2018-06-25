package com.bots.lvivcroissantbot.service.telegram;

import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;

public interface CallBackParserService {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
