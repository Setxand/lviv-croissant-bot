package com.bots.lvivcroissantbot.service;

import com.bots.lvivcroissantbot.dto.uni.CroissantDTO;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.repository.CroissantEntityRepository;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CroissantServiceTestController {
    Logger logger = LoggerFactory.getLogger(CroissantServiceTestController.class);
    private CroissantService croissantService;
    @Mock
    private CroissantEntityRepository croissantEntityRepository;
    private static final Long testId = 1L;

    @Before
    public void setup() {
        croissantService = new CroissantService(croissantEntityRepository);
    }


    @Test
    public void test1FindById() {
        // Given
        CroissantEntity expected = croissantEntityInit();
        when(croissantEntityRepository.findById(testId)).thenReturn(Optional.of(expected));

        // When
        Optional<CroissantDTO> result = croissantService.findById(testId);

        // Then
        assertTrue(result.isPresent());
        CroissantDTO resultDTO = result.get();

        assertEquals(expected.getName(), resultDTO.getName());
        assertEquals(expected.getPrice(), resultDTO.getPrice().intValue());
    }

    @Test
    public void test2PostCroissant() {
        // Given
        CroissantDTO croissantDTO = croissantDtoInit();

        // When
        croissantService.createCroissant(croissantDTO);

        // Then
        ArgumentCaptor<CroissantEntity> captor = ArgumentCaptor.forClass(CroissantEntity.class);
        verify(croissantEntityRepository).saveAndFlush(captor.capture());
        CroissantEntity objectPassed = captor.getValue();

        assertEquals(croissantDTO.getName(), objectPassed.getName());
        assertEquals(croissantDTO.getPrice().intValue(), objectPassed.getPrice());
    }

    @Test
    public void test3GetCroissants() {
        //    when(croissantEntityRepository.findAll()).thenReturn(Arrays.asList(croissantEntity));
        //  assertEquals(Arrays.asList(croissantDTO), croissantService.getCroissants());
    }

    @Test
    public void test4PutCroissant() {
        //Given
        CroissantDTO croissantDTO = croissantDtoInit();
        //When
        when(croissantEntityRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(croissantEntityInit()));
        croissantService.putCroissant(croissantDTO, testId);
        //Then
        ArgumentCaptor<CroissantEntity> captor = ArgumentCaptor.forClass(CroissantEntity.class);
        verify(croissantEntityRepository).saveAndFlush(captor.capture());
        CroissantEntity passer = captor.getValue();

        assertEquals(croissantDTO.getName(),passer.getName());
        assertEquals(croissantDTO.getPrice().intValue(), passer.getPrice());
    }



    private CroissantEntity croissantEntityInit() {
        CroissantEntity croissantEntity = new CroissantEntity();
        croissantEntity.setName("Круасан галицький");
        croissantEntity.setType("SANDWICH");
        croissantEntity.setPrice(33);
        croissantEntity.setId(8L);
        return croissantEntity;

    }

    private CroissantDTO croissantDtoInit() {
        CroissantDTO croissantDTO = new CroissantDTO();
        croissantDTO.setName("Круасан галицький");
        croissantDTO.setType("SANDWICH");
        croissantDTO.setPrice(33);
        return croissantDTO;
    }
}
