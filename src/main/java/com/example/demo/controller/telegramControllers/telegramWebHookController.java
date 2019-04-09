package com.example.demo.controller.telegramControllers;

import com.example.demo.model.telegram.Update;
import com.example.demo.services.telegramService.UpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegramWebHook")
public class telegramWebHookController {
	@Autowired
	private UpdateParserService updateParserService;

	@PostMapping
	public void getUpdate(@RequestBody Update update) {
		updateParserService.parseUpdate(update);
	}
}
