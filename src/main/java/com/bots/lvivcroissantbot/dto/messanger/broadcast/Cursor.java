package com.bots.lvivcroissantbot.dto.messanger.broadcast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cursor {
    private String before;
    private String after;
}
