package com.bots.lvivCroissantBot.service.repository;

import com.bots.lvivCroissantBot.entity.SpeakingMessage;

public interface SpeakingMessagesRepositoryService {
    public SpeakingMessage findByKey(String key);
    public SpeakingMessage saveAndFlush(SpeakingMessage speakingMessage);
    public void delete(SpeakingMessage speakingMessage);

}
