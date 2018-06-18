package com.example.demo.service.peopleRegisterService;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.Role;

import java.util.List;

public interface UserRepositoryService {
    public List<MUser> findAll();
    public MUser findOnebyRId(Long id);
    public MUser findTop();
    public void saveAndFlush(MUser MUser);
    public void remove(MUser MUser);
    public List<MUser> getByRole(Role role);
}
