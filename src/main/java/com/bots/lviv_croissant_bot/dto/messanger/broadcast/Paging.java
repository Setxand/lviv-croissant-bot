package com.bots.lviv_croissant_bot.dto.messanger.broadcast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Paging {
    private Cursor cursors;

    @Override
    public String toString() {
        return "Paging{" +
                "cursors=" + cursors +
                '}';
    }
}
