package com.example.demo.models.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Payload {
	@JsonProperty("template_type")
	private String templateType = "generic";
	@JsonProperty("top_element_style")
	private String topElementStyle;
	private String text;
	private List<Button> buttons = new ArrayList<>();
	private List<Element> elements = new ArrayList<>();

	public Payload(List<Element> elements) {
		this.elements = elements;
	}

	public Payload() {
	}


}
