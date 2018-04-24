package com.example.demo.models.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Message {
    @JsonProperty("message_id")
    private Integer messageId;
    private User from;
    private Integer date;
    private Chat chat;
    private String text;

}
