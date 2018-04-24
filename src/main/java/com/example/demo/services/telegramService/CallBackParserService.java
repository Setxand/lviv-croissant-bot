package com.example.demo.services.telegramService;

import com.example.demo.models.telegram.CallBackQuery;

public interface CallBackParserService {
    public void parseCallBackQuery(CallBackQuery callBackQuery);
}
