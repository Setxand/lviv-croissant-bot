package com.bots.lvivCroissantBot.service.repository;

import com.bots.lvivCroissantBot.entity.Support;

public interface SupportEntityRepositoryService {
    public Support getByUserId(Long userId);
    public Support saveAndFlush(Support support);
    public void remove(Support support);

}
