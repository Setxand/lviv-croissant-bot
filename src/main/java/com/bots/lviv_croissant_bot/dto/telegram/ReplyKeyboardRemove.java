package com.bots.lviv_croissant_bot.dto.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.button.Markup;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplyKeyboardRemove implements Markup{
    @JsonProperty("remove_keyboard")
    private Boolean removeKeyboard;

    public ReplyKeyboardRemove(Boolean removeKeyboard) {
        this.removeKeyboard = removeKeyboard;
    }
}
