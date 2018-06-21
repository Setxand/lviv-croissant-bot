package com.bots.lvivCroissantBot.service.repositoryService;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;

import java.util.List;

public interface CustomerOrderingRepositoryService {
    public List<CustomerOrdering> findAll();
    public CustomerOrdering findOne(Long id);
    public CustomerOrdering findByPhoneNumber(String phoneNumber);
    public CustomerOrdering findTop();
    public void saveAndFlush(CustomerOrdering customerOrdering);
    public void delete(CustomerOrdering customerOrdering);
    public CustomerOrdering findTopByUser(MUser MUser);
    public CustomerOrdering findTopByTUser(TUser tUser);
}
