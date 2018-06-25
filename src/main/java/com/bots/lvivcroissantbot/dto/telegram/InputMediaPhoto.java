package com.bots.lvivcroissantbot.dto.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InputMediaPhoto implements InputMedia {
    private String type;
    private String media;
    private String caption;
    @JsonProperty("parse_mode")
    private String parseMode;
}
