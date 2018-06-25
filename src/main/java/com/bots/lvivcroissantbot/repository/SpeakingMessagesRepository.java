package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.SpeakingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakingMessagesRepository extends JpaRepository<SpeakingMessage, String> {

}
