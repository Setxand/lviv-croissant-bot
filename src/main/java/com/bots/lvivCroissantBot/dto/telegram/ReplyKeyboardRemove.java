package com.bots.lvivCroissantBot.dto.telegram;

import com.bots.lvivCroissantBot.dto.telegram.button.Markup;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplyKeyboardRemove implements Markup{
    @JsonProperty("remove_keyboard")
    private Boolean removeKeyboard;

    public ReplyKeyboardRemove(Boolean removeKeyboard) {
        this.removeKeyboard = removeKeyboard;
    }
}
