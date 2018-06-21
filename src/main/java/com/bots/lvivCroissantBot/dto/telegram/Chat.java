package com.bots.lvivCroissantBot.dto.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Chat {
    private Integer id;
    private String type;
    private String title;
    @JsonProperty("username")
    private String userName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String text;

    public Chat(Integer id) {
        this.id = id;
    }
}
