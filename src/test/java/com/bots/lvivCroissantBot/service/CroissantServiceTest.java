package com.bots.lvivCroissantBot.service;

import com.bots.lvivCroissantBot.dto.uni.CroissantDTO;
import com.bots.lvivCroissantBot.repository.CroissantEntityRepository;
import com.bots.lvivCroissantBot.service.uniService.CroissantService;
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
