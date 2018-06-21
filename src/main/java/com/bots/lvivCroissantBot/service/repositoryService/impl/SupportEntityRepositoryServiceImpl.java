package com.bots.lvivCroissantBot.service.repositoryService.impl;

import com.bots.lvivCroissantBot.entity.SupportEntity;
import com.bots.lvivCroissantBot.repository.SupportEntityRepository;
import com.bots.lvivCroissantBot.service.repositoryService.SupportEntityRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportEntityRepositoryServiceImpl implements SupportEntityRepositoryService {
    @Autowired
    private SupportEntityRepository supportEntityRepository;
    @Override
    public SupportEntity getByUserId(Long userId) {
        return supportEntityRepository.findByUserId(userId);
    }

    @Override
    public SupportEntity saveAndFlush(SupportEntity supportEntity) {
        return supportEntityRepository.saveAndFlush(supportEntity);
    }

    @Override
    public void remove(SupportEntity supportEntity) {
        supportEntityRepository.delete(supportEntity);
    }
}
