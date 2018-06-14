package com.example.demo.service.telegramService;

import com.example.demo.dto.telegram.Update;

public interface UpdateParserService {
    public void parseUpdate(Update update);
}
