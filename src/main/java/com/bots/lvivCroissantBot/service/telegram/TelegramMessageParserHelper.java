package com.bots.lvivCroissantBot.service.telegram;

import com.bots.lvivCroissantBot.dto.telegram.CallBackQuery;
import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramMessageParserHelper {
    void helpStart(Message message);

    void helpDeleteOrderings(Message message);

    void helpCreateOwnCroissant(Message message);

    void helpEnterPhoneInStart(Message message);

    void helpReinputData(CallBackQuery callBackQuery);
}
