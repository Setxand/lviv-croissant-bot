package com.bots.lviv_croissant_bot.service.support.Impl;

import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.service.peopleRegister.MUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.RecognizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;
@Service
public class RecognizeServiceImpl implements RecognizeService {
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Override
    public String recognize(String text,Long userId) {
        MUser MUser = MUserRepositoryService.findOnebyRId(userId);
        return ResourceBundle.getBundle("dictionary", MUser.getLocale()).getString(text);
    }
}
