package com.bots.lvivCroissantBot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class MenuItem {
    private String type;
    private String title;
    private String url;
    private String payload;
    @JsonProperty("call_to_actions")
    private List<MenuItem> callToActions;

    public MenuItem() {
    }

    public MenuItem(String type, String title, String payload) {
        this.type = type;
        this.title = title;
        this.payload = payload;
    }
    public MenuItem(String type, String title) {
        this.type = type;
        this.title = title;

    }
}
