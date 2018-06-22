package com.bots.lviv_croissant_bot.service.support;

import com.bots.lviv_croissant_bot.entity.register.MUser;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
    public void sendMailForAdminAboutMark(MUser MUser, String mark) throws MessagingException, MalformedURLException;
}
