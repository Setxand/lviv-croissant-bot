package com.bots.lvivCroissantBot.service.repositoryService.impl;

import com.bots.lvivCroissantBot.entity.SpeakingMessage;
import com.bots.lvivCroissantBot.repository.SpeakingMessagesRepository;
import com.bots.lvivCroissantBot.service.repositoryService.SpeakingMessagesRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpeakingMessagesRepositoryServiceImpl implements SpeakingMessagesRepositoryService {
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepository;
    @Override
    public SpeakingMessage findByKey(String key) {
        return speakingMessagesRepository.findOne(key);
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
