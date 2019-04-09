package com.example.demo.model.telegram.buttons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InlineKeyboardButton implements Button {
	private String text;
	private String url;
	@JsonProperty("callback_data")
	private String callBackData;

	public InlineKeyboardButton(String text, String callBackData) {
		this.text = text;
		this.callBackData = callBackData;
	}
}
