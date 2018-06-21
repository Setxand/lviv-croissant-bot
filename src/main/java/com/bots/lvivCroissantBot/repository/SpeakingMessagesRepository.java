package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.SpeakingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakingMessagesRepository extends JpaRepository<SpeakingMessage,String>{

}
