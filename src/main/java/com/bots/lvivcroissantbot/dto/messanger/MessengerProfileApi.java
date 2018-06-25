package com.bots.lvivcroissantbot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class MessengerProfileApi {
    @JsonProperty("get_started")
    private GetStarted getStarted;
    @JsonProperty("persistent_menu")
    private List<PersistentMenu> persistentMenu;

    public MessengerProfileApi(GetStarted getStarted, List<PersistentMenu> persistentMenu) {
        this.getStarted = getStarted;
        this.persistentMenu = persistentMenu;
    }

    public MessengerProfileApi() {
    }


}
