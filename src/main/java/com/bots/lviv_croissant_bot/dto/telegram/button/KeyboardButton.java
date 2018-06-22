package com.bots.lviv_croissant_bot.dto.telegram.button;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeyboardButton implements Button {
    private String text;

    public KeyboardButton(String text) {
        this.text = text;
    }
}
