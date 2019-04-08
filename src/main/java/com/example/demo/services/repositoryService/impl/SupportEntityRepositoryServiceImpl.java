package com.example.demo.services.repositoryService.impl;

import com.example.demo.entities.SupportEntity;
import com.example.demo.repository.SupportEntityRepository;
import com.example.demo.services.repositoryService.SupportEntityRepositoryService;
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
