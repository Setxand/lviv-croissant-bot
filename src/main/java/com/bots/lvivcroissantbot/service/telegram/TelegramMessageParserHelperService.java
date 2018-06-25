package com.bots.lvivcroissantbot.service.telegram;

import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;
import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramMessageParserHelperService {
    void helpStart(Message message);

    void helpDeleteOrderings(Message message);

    void helpCreateOwnCroissant(Message message);

    void helpEnterPhoneInStart(Message message);

    void helpReinputData(CallBackQuery callBackQuery);
}
