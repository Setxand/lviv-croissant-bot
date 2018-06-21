package com.bots.lvivCroissantBot.service.telegramService;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;

public interface CallBackParserService {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
