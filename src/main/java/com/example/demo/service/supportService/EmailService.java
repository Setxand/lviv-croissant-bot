package com.example.demo.service.supportService;

import com.example.demo.entity.peopleRegister.MUser;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
    public void sendMailForAdminAboutMark(MUser MUser, String mark) throws MessagingException, MalformedURLException;
}
