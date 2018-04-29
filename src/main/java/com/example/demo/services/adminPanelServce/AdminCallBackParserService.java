package com.example.demo.services.adminPanelServce;

import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.Update;

public interface AdminCallBackParserService {
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery);
}
