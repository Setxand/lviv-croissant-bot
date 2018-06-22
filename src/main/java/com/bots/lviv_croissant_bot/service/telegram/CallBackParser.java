package com.bots.lviv_croissant_bot.service.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;

public interface CallBackParser {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
