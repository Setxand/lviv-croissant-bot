package com.example.demo.services.supportService;

import com.example.demo.entities.peopleRegister.User;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public interface EmailService {
	public void sendMailForAdminAboutMark(User user, String mark) throws MessagingException, MalformedURLException;
}
