package com.bots.lvivCroissantBot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Sender {
    private Long id;
    private UserData name;
    public Sender(Long id) {
        this.id = id;
    }
    public Sender(){}



}
