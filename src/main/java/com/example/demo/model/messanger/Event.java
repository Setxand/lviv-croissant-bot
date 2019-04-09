package com.example.demo.model.messanger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Event {
	private String object;
	private List<Entry> entry;

	public Event() {
	}

}
