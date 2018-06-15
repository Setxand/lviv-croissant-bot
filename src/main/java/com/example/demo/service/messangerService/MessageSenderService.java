package com.example.demo.service.messangerService;

import com.example.demo.dto.messanger.Button;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.dto.messanger.QuickReply;
import com.example.demo.dto.messanger.UserData;

import java.util.List;

public interface MessageSenderService {
    public void sendMessage(Messaging messaging);
    public void errorMessage(Long recipient);
    public void sendSimpleMessage(String text,Long recipient);
    public void askForCourierActions(Long recipient);
    public void sendSimpleQuestion(Long recipient,String text,String payload,String splitter);

    public void askTypeOfCroissants(Long recipient,String payload );
    public void askCroissantName(Messaging messaging);
    public void askSelectLanguage(Long recipient);
    public UserData sendFacebookRequest(Long recipient);
    public void sendUserActions(Long recipient);
    public  void sendButtons(List<Button> buttons, String text, Long recipient);
    public void sendQuickReplies(List<QuickReply>quickReplies,String text,Long recipient);
}
