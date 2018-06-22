package com.bots.lviv_croissant_bot.service;

import com.bots.lviv_croissant_bot.dto.uni.CroissantDTO;
import com.bots.lviv_croissant_bot.repository.CroissantEntityRepository;
import com.bots.lviv_croissant_bot.service.uni.CroissantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CroissantServiceTest {
    private CroissantService croissantService;
    @Mock
    CroissantEntityRepository croissantEntityRepository;

    @Before
    public void setup() {
        croissantService = new CroissantService(croissantEntityRepository);
    }

    @Test
    public void test1() {
        croissantService.putCroissant(new CroissantDTO(), 1L);
    }
}
