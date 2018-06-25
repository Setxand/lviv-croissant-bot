package com.bots.lvivcroissantbot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SpeakingMessage {

    @Id
    private String id;
    private String message;

    public SpeakingMessage(String id) {
        this.id = id;
    }

    public SpeakingMessage(String id, String message) {
        this.id = id;
        this.message = message;
    }
}
