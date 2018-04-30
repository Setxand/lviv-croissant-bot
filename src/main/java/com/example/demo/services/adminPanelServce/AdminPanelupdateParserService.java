package com.example.demo.services.adminPanelServce;

import com.example.demo.models.telegram.Update;

public interface AdminPanelUpdateParserService {
    public  void parseUpdate(Update update);
}
