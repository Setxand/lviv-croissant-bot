package com.bots.lvivCroissantBot.dto.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class CustomLabel {
    private String name;
    @JsonProperty("MUser")
    private Long userId;
    private Long id;
    public CustomLabel(String name) {
        this.name = name;
    }
}
