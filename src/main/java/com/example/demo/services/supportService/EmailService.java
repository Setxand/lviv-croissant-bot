package com.example.demo.services.supportService;

import com.example.demo.entity.peopleRegister.User;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
	public void sendMailForAdminAboutMark(User user, String mark) throws MessagingException, MalformedURLException;
}
