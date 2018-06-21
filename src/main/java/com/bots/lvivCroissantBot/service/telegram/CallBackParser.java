package com.bots.lvivCroissantBot.service.telegram;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;

public interface CallBackParser {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
