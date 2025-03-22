package com.example.roaming_cdr_service.service;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.Subscriber;
import com.example.roaming_cdr_service.repository.CDRRepository;
import com.example.roaming_cdr_service.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса {@link CDRService}.
 */
class CDRServiceTest {

    @Mock
    private CDRRepository cdrRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private CDRService cdrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для метода {@link CDRService#generateCDRs()}.
     * Проверяет успешную генерацию CDR.
     */
    @Test
    void testGenerateCDRs_Success() {
        // Подготовка данных
        when(subscriberRepository.findAll()).thenReturn(Collections.singletonList(new Subscriber("79991112233")));

        // Вызов метода
        cdrService.generateCDRs();

        // Проверка результата
        verify(cdrRepository, atLeastOnce()).save(any(CDR.class));
    }

    /**
     * Тест для метода {@link CDRService#generateCDRs()}.
     * Проверяет обработку ошибки при пустом списке абонентов.
     */
    @Test
    void testGenerateCDRs_NoSubscribers() {
        // Подготовка данных
        when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());

        // Вызов метода и проверка исключения
        assertThrows(IllegalStateException.class, () -> cdrService.generateCDRs());
    }
}