package com.bots.lvivcroissantbot.dto.messanger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Entry {
    private String id;
    private Long time;
    private List<Messaging> messaging;

    public Entry() {
    }
}
