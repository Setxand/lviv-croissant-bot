package com.example.demo.services.repositoryService;

import com.example.demo.entity.SupportEntity;

public interface SupportEntityRepositoryService {
	public SupportEntity getByUserId(Long userId);

	public SupportEntity saveAndFlush(SupportEntity supportEntity);

	public void remove(SupportEntity supportEntity);

}
