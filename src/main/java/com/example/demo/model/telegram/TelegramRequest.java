package com.example.demo.model.telegram;

import com.example.demo.model.telegram.buttons.Markup;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramRequest {
	String photo;
	String caption;
	private String url;
	private String text;
	@JsonProperty("chat_id")
	private Integer chatId;
	@JsonProperty("reply_markup")
	private Markup markup;

	public TelegramRequest(String text, Integer chatId) {
		this.text = text;
		this.chatId = chatId;
	}

	public TelegramRequest(Integer chatId, Markup markup, String photo, String caption) {
		this.chatId = chatId;
		this.markup = markup;
		this.photo = photo;
		this.caption = caption;
	}
}
