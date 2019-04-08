package com.example.demo.services.messangerService;

import com.example.demo.models.messanger.Button;
import com.example.demo.models.messanger.Messaging;
import com.example.demo.models.messanger.QuickReply;
import com.example.demo.models.messanger.UserData;

import java.util.List;

public interface MessageSenderService {
	public void sendMessage(Messaging messaging);

	public void errorMessage(Long recipient);

	public void sendSimpleMessage(String text, Long recipient);

	public void askForCourierActions(Long recipient);

	public void sendSimpleQuestion(Long recipient, String text, String payload, String splitter);

	public void askTypeOfCroissants(Long recipient, String payload);

	public void askCroissantName(Messaging messaging);

	public void askSelectLanguage(Long recipient);

	public UserData sendFacebookRequest(Long recipient);

	public void sendUserActions(Long recipient);

	public void sendButtons(List<Button> buttons, String text, Long recipient);

	public void sendQuickReplies(List<QuickReply> quickReplies, String text, Long recipient);
}
