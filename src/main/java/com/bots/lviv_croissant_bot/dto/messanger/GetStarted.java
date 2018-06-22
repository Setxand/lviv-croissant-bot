package com.bots.lviv_croissant_bot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetStarted {
    private String payload;

    public GetStarted(String payload) {
        this.payload = payload;
    }

    public GetStarted() {
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
