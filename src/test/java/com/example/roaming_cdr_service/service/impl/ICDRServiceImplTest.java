package com.example.roaming_cdr_service.service.impl;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.Subscriber;
import com.example.roaming_cdr_service.repository.CDRRepository;
import com.example.roaming_cdr_service.repository.SubscriberRepository;
import com.example.roaming_cdr_service.service.impl.CDRServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса {@link CDRServiceImpl}.
 */
class ICDRServiceImplTest {

    @Mock
    private CDRRepository cdrRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private CDRServiceImpl cdrServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для метода {@link CDRServiceImpl#generateCDRs()}.
     * Проверяет успешную генерацию CDR.
     */
    @Test
    void testGenerateCDRs_Success() {
        // Подготовка данных - теперь два абонента, а не один
        List<Subscriber> mockSubscribers = List.of(
                new Subscriber("79991112233"),
                new Subscriber("79992221122")
        );
        when(subscriberRepository.findAll()).thenReturn(mockSubscribers);

        // Вызов метода
        cdrServiceImpl.generateCDRs();

        // Проверка результата
        verify(cdrRepository, atLeastOnce()).saveAll(anyList());
    }

    /**
     * Тест для метода {@link CDRServiceImpl#generateCDRs()}.
     * Проверяет обработку ошибки при пустом списке абонентов.
     */
    @Test
    void testGenerateCDRs_NoSubscribers() {
        // Подготовка данных
        when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());

        // Вызов метода и проверка исключения
        assertThrows(IllegalStateException.class, () -> cdrServiceImpl.generateCDRs());
    }
}