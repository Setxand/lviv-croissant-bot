package com.bots.lvivCroissantBot.service.supportService;

import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
    public void sendMailForAdminAboutMark(MUser MUser, String mark) throws MessagingException, MalformedURLException;
}
