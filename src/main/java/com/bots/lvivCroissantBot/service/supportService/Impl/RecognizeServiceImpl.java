package com.bots.lvivCroissantBot.service.supportService.Impl;

import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.supportService.RecognizeService;
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
