package com.bots.lvivcroissantbot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class Button {

    private String type;
    private String url;
    private String title;
    private String payload;
    @JsonProperty("webview_height_ratio")
    private String heightRatio;
    @JsonProperty("messenger_extensions")
    private Boolean mesExtentions;

    public Button() {
    }

    public Button(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public Button(String type, String title, String payload) {
        this.type = type;
        this.title = title;
        this.payload = payload;
    }
}
