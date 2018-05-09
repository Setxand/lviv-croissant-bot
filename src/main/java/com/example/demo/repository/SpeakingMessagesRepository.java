package com.example.demo.repository;

import com.example.demo.entities.SpeakingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakingMessagesRepository extends JpaRepository<SpeakingMessage,String>{

}
