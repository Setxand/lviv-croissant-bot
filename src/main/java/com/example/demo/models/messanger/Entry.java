package com.example.demo.models.messanger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Entry {
	private String id;
	private Long time;
	private List<Messaging> messaging;

	public Entry() {
	}
}
