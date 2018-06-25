package com.bots.lvivcroissantbot.service.support.impl;

import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
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
