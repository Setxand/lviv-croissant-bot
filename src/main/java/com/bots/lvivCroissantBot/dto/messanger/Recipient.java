package com.bots.lvivCroissantBot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Recipient {
    private Long id;
    private UserData name;
    @JsonProperty("phone_number")
    private String phoneNumber;
    public Recipient() {
    }
    public Recipient(Long id){
        this.id = id;
    }


}