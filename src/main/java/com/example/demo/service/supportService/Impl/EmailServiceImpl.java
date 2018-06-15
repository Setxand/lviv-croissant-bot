package com.example.demo.service.supportService.Impl;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import com.example.demo.service.supportService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;

import static com.example.demo.constantEnum.messengerEnums.Roles.ADMIN;


    @Service
    public class EmailServiceImpl implements EmailService {
        @Autowired
        private JavaMailSender emailSender;
        @Autowired
        UserRepositoryService userRepositoryService;
        @Override
        public void sendMailForAdminAboutMark(MUser MUser, String mark) throws MessagingException, MalformedURLException {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            String htmlMsg = "<h3>"+ MUser.getName()+" "+ MUser.getLastName()+" Поставив вам оцінку: "+mark+"</h3><img height = '80px' width = '80px' src = '"+ MUser.getPicture()+"' />" +
                    "<p>Перейти до бота:</p><a href = 'https://www.facebook.com/messages/t/374868892974038'>" +
                    "<img src = 'https://scontent.fiev1-1.fna.fbcdn.net/v/t1.0-1/p80x80/24796344_374868999640694_1149758083912015718_n.png?oh=1012977d613ce748a3dac157c74c6e60&oe=5B4BFC1B'/></a>";
            message.setContent(htmlMsg,"text/html; charset=utf-8");
            helper.setSubject("Lviv - Croissants bot щойно поставили оцінку!");
            for(MUser admin: userRepositoryService.getByRole(ADMIN)){
                helper.setTo(admin.getEmail());
                emailSender.send(message);
            }


        }


}
