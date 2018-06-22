package com.bots.lviv_croissant_bot.dto.messanger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class Event {
    private String object;
    private List<Entry> entry;

    public Event(){}

}
