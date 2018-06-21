package com.bots.lvivCroissantBot.service.repository.impl;

import com.bots.lvivCroissantBot.entity.Support;
import com.bots.lvivCroissantBot.repository.SupportEntityRepository;
import com.bots.lvivCroissantBot.service.repository.SupportEntityRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportEntityRepositoryServiceImpl implements SupportEntityRepositoryService {
    @Autowired
    private SupportEntityRepository supportEntityRepository;
    @Override
    public Support getByUserId(Long userId) {
        return supportEntityRepository.findByUserId(userId);
    }

    @Override
    public Support saveAndFlush(Support support) {
        return supportEntityRepository.saveAndFlush(support);
    }

    @Override
    public void remove(Support support) {
        supportEntityRepository.delete(support);
    }
}
