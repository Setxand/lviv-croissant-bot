package com.example.demo.models.telegram;

import com.example.demo.enums.Platform;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CallBackQuery {
    private Long id;
    private User from;
    private Message message;
    @JsonProperty("chat_instance")
    private Long chatInstance;
    private String data;
    private Platform platform;
}
