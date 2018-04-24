package com.example.demo.models.telegram.buttons;

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
