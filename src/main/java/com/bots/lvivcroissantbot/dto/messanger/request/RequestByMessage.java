package com.bots.lvivcroissantbot.dto.messanger.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestByMessage {
    @JsonProperty("recipient_id")
    private String recipientId;
    @JsonProperty("message_id")
    private String mid;

    @Override
    public String toString() {
        return "\n\nRequestByMessage{" +
                "recipientId=" + recipientId +
                ", mid='" + mid + '\'' +
                '}'+"\n";
    }
}
