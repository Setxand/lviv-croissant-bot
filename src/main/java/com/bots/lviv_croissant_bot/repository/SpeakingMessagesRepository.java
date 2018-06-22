package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.entity.SpeakingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakingMessagesRepository extends JpaRepository<SpeakingMessage,String>{

}
