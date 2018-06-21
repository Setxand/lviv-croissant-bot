package com.bots.lvivCroissantBot.service.support;

import com.bots.lvivCroissantBot.entity.register.MUser;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
    public void sendMailForAdminAboutMark(MUser MUser, String mark) throws MessagingException, MalformedURLException;
}
