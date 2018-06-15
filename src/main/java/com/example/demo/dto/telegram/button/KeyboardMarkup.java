package com.example.demo.dto.telegram.button;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class KeyboardMarkup implements Markup {
    private List<List<KeyboardButton>> keyboard;

    public KeyboardMarkup(List<List<KeyboardButton>> keyboard) {
        this.keyboard = keyboard;
    }
}

