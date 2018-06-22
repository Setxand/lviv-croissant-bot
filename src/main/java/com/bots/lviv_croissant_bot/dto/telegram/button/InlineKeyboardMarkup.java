package com.bots.lviv_croissant_bot.dto.telegram.button;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class InlineKeyboardMarkup implements Markup {
    @JsonProperty("inline_keyboard")
    private List<List<InlineKeyboardButton>> inlineKeyBoard;

    public InlineKeyboardMarkup(List<List<InlineKeyboardButton>> inlineKeyBoard) {
        this.inlineKeyBoard = inlineKeyBoard;
    }


}
