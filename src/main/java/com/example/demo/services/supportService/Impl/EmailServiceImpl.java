package com.example.demo.services.supportService.Impl;

import com.example.demo.entities.peopleRegister.User;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;

import static com.example.demo.enums.messengerEnums.Roles.ADMIN;


@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	UserRepositoryService userRepositoryService;
	@Autowired
	private JavaMailSender emailSender;

	@Override
	public void sendMailForAdminAboutMark(User user, String mark) throws MessagingException, MalformedURLException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		String htmlMsg = "<h3>" + user.getName() + " " + user.getLastName() + " Поставив вам оцінку: " + mark + "</h3><img height = '80px' width = '80px' src = '" + user.getPicture() + "' />" +
				"<p>Перейти до бота:</p><a href = 'https://www.facebook.com/messages/t/374868892974038'>" +
				"<img src = 'https://scontent.fiev1-1.fna.fbcdn.net/v/t1.0-1/p80x80/24796344_374868999640694_1149758083912015718_n.png?oh=1012977d613ce748a3dac157c74c6e60&oe=5B4BFC1B'/></a>";
		message.setContent(htmlMsg, "text/html; charset=utf-8");
		helper.setSubject("Lviv - Croissants bot щойно поставили оцінку!");
		for (User admin : userRepositoryService.getByRole(ADMIN)) {
			helper.setTo(admin.getEmail());
			emailSender.send(message);
		}


	}


}
