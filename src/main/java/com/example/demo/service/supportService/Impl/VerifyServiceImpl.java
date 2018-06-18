package com.example.demo.service.supportService.Impl;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.service.peopleRegisterService.MUserRepositoryService;
import com.example.demo.service.supportService.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class VerifyServiceImpl implements VerifyService {
    @Autowired
    private MUserRepositoryService MUserRepositoryService;

    @Value("${app.verify.token}")
    private String VER_TOK;
    @Override
    public boolean verify(String verifyToken) {
        if(verifyToken.equals(VER_TOK))
            return true;
        else
            return false;
    }


    @Override
    public  boolean isCustomer(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        if(MUser.getEmail()==null || MUser.getName() == null || MUser.getUser().getPhoneNumber() == null || MUser.getAddress() == null)
            return false;


        return true;
    }
}
