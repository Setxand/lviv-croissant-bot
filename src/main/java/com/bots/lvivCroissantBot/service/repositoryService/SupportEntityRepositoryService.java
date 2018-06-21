package com.bots.lvivCroissantBot.service.repositoryService;

import com.bots.lvivCroissantBot.entity.SupportEntity;

public interface SupportEntityRepositoryService {
    public SupportEntity getByUserId(Long userId);
    public SupportEntity saveAndFlush(SupportEntity supportEntity);
    public void remove(SupportEntity supportEntity);

}
