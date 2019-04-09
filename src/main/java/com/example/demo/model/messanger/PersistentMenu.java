package com.example.demo.model.messanger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersistentMenu {
	private String locale;
	@JsonProperty("composer_input_disabled")
	private Boolean composerInputDisabled;
	@JsonProperty("call_to_actions")
	private List<MenuItem> callToActions = new ArrayList<>();

	public PersistentMenu() {
		this.locale = "default";
		this.composerInputDisabled = false;

	}
}
