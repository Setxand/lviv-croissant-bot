package com.bots.lviv_croissant_bot.service;

import com.bots.lviv_croissant_bot.dto.uni.CroissantDTO;
import com.bots.lviv_croissant_bot.entity.lvivCroissants.CroissantEntity;
import com.bots.lviv_croissant_bot.repository.CroissantEntityRepository;
import com.bots.lviv_croissant_bot.service.uni.CroissantService;
import com.bots.lviv_croissant_bot.tools.CroissantUtilManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CroissantServiceTest {
    private CroissantService croissantService;
    @Mock
    private CroissantEntityRepository croissantEntityRepository;

    private static final Long testId = 1L;
    private CroissantEntity croissantEntity;
    private CroissantDTO croissantDTO;

    @Before
    public void setup() {
        croissantDtoInit();
        croissantService = new CroissantService(croissantEntityRepository);

        croissantEntity = new CroissantEntity();
        croissantEntity.setName("Круасан галицький");
        croissantEntity.setType("SANDWICH");
        croissantEntity.setPrice(33);
        croissantEntity.setId(8L);


        when(croissantEntityRepository.findById(testId)).thenReturn(Optional.of(croissantEntity));

        when(croissantEntityRepository.saveAndFlush(croissantEntity)).thenReturn(croissantEntity);

    }

    private void croissantDtoInit() {


        croissantDTO= new CroissantDTO();
        croissantDTO.setName("Круасан галицький");
        croissantDTO.setType("SANDWICH");
        croissantDTO.setPrice(33);
//        croissantDTO.setId(8L);
    }

    @Test
    public void test1FindById() {

        assertEquals(Optional.ofNullable(CroissantUtilManager.croissantEntityToDTO(croissantEntity)),croissantService.findById(testId));
    }
    @Test
    public void test2PutCroissant(){
        assertEquals(CroissantUtilManager.croissantEntityToDTO(croissantEntity),croissantService.createCroissant(croissantDTO));
    }
    @Test
    public void test3(){
        croissantService.getCroissants();
    }
}
