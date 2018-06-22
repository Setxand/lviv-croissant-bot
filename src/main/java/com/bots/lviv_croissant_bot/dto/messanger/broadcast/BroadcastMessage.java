package com.bots.lviv_croissant_bot.dto.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class BroadcastMessage {
    @JsonProperty("message_creative_id")
    private Long messageCreativeId;
    @JsonProperty("notification_type")
    private String notificationType;
    private String tag;
    @JsonProperty("custom_label_id")
    private Long customLabelId;

    public BroadcastMessage(Long messageCreativeId) {
        this.messageCreativeId = messageCreativeId;
    }
}
