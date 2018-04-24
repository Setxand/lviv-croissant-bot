package com.example.demo.services.lvivCroissantRepositoryService;

import com.example.demo.entities.lvivCroissants.SupportEntity;

public interface SupportEntityRepositoryService {
    public SupportEntity getByUserId(Long userId);
    public SupportEntity saveAndFlush(SupportEntity supportEntity);
    public void remove(SupportEntity supportEntity);

}
