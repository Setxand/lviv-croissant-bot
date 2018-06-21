package com.bots.lvivCroissantBot.service.repositoryService.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.repository.CustomerOrderingRepository;
import com.bots.lvivCroissantBot.service.repositoryService.CustomerOrderingRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerOrderingRepositoryServiceImpl implements CustomerOrderingRepositoryService {

    @Autowired
    private CustomerOrderingRepository customerOrderingRepository;


    @Override
    public List<CustomerOrdering> findAll() {
        return customerOrderingRepository.findAll();
    }

    @Override
    public CustomerOrdering findOne(Long id) {
        return customerOrderingRepository.findOne(id);
    }

    @Override
    public CustomerOrdering findByPhoneNumber(String phoneNumber) {
        return customerOrderingRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public CustomerOrdering findTop() {
        return customerOrderingRepository.findTopByOrderByIdDesc();
    }

    @Override
    public void saveAndFlush(CustomerOrdering customerOrdering) {
        customerOrderingRepository.saveAndFlush(customerOrdering);
    }



    @Override
    public void delete(CustomerOrdering customerOrdering) {
        customerOrderingRepository.delete(customerOrdering);
    }

    @Override
    public CustomerOrdering findTopByUser(MUser MUser) {
        return customerOrderingRepository.findTopByMUserOrderByIdDesc(MUser);
    }

    @Override
    public CustomerOrdering findTopByTUser(TUser tUser) {
        return customerOrderingRepository.findTopByTUserOrderByIdDesc(tUser);
    }

}
