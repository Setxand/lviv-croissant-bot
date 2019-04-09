package com.example.demo.controller.telegramControllers;

import com.example.demo.services.adminPanelService.AdminPanelUpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import telegram.Update;

@RestController
@RequestMapping("/adminPanel")
public class telegramAdminPanelWebhookController {
	@Autowired
	private AdminPanelUpdateParserService adminPanelUpdateParserService;

	@PostMapping
	public void getUpdate(@RequestBody Update update) {
		adminPanelUpdateParserService.parseUpdate(update);
	}
}
