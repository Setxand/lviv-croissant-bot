package com.example.demo.dto.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DynamicText {
    private String text;
    @JsonProperty("fallback_text")
    private String fallBackText;

    public DynamicText(String text, String fallBackText) {
        this.text = text;
        this.fallBackText = fallBackText;
    }
}
