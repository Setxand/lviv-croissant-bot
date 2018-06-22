package com.bots.lviv_croissant_bot.service.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;

public interface CallBackParserService {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
