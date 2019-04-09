package com.example.demo.model.telegram;

import com.example.demo.model.telegram.buttons.Markup;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplyKeyboardRemove implements Markup {
	@JsonProperty("remove_keyboard")
	private Boolean removeKeyboard;

	public ReplyKeyboardRemove(Boolean removeKeyboard) {
		this.removeKeyboard = removeKeyboard;
	}
}
