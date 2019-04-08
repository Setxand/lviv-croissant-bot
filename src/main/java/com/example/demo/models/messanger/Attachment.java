package com.example.demo.models.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class Attachment {
	private String type;
	private Payload payload;


	public Attachment(String type, Payload payload) {
		this.type = type;
		this.payload = payload;
	}

	public Attachment() {
	}


}
