package com.bots.lvivCroissantBot.service.repository.impl;

import com.bots.lvivCroissantBot.entity.SpeakingMessage;
import com.bots.lvivCroissantBot.exception.ElementNoFoundException;
import com.bots.lvivCroissantBot.repository.SpeakingMessagesRepository;
import com.bots.lvivCroissantBot.service.repository.SpeakingMessagesRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpeakingMessagesRepositoryServiceImpl implements SpeakingMessagesRepositoryService {
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepository;
    @Override
    public SpeakingMessage findByKey(String key) {
        return speakingMessagesRepository.findById(key).orElseThrow(ElementNoFoundException::new);
    }

    @Override
    public SpeakingMessage saveAndFlush(SpeakingMessage speakingMessage) {
        return speakingMessagesRepository.saveAndFlush(speakingMessage);
    }

    @Override
    public void delete(SpeakingMessage speakingMessage) {
        speakingMessagesRepository.delete(speakingMessage);
    }
}
