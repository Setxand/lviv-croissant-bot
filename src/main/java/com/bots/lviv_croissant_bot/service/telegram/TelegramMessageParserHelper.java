package com.bots.lviv_croissant_bot.service.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;
import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface TelegramMessageParserHelper {
    void helpStart(Message message);

    void helpDeleteOrderings(Message message);

    void helpCreateOwnCroissant(Message message);

    void helpEnterPhoneInStart(Message message);

    void helpReinputData(CallBackQuery callBackQuery);
}
