package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.UDR;
import com.example.roaming_cdr_service.repository.CDRRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса {@link UDRController}.
 * Тесты проверяют корректность работы методов контроллера, включая обработку успешных сценариев и ошибок.
 */
class UDRControllerTest {

    private static final String VALID_MSISDN = "79991112233";
    private static final String OTHER_MSISDN = "79992223344";
    private static final String VALID_MONTH = "2025-02";
    private static final String INVALID_MONTH = "invalid-month";
    @Mock
    private CDRRepository cdrRepository;

    @InjectMocks
    private UDRController udrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для метода {@link UDRController#getUDR(String, String)}.
     * Проверяет успешное получение UDR.
     * Ожидается, что метод вернет корректный UDR объект с данными о звонках.
     */
    @Test
    void testGetUDR_Success() {
        // Подготовка данных
        CDR cdr = CDR.builder()
                .callType("01")
                .msisdn(VALID_MSISDN)
                .otherMsisdn(OTHER_MSISDN)
                .callStartTime(LocalDateTime.now())
                .callEndTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.singletonList(cdr));
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        // Вызов метода
        UDR udr = udrController.getUDR(VALID_MSISDN, VALID_MONTH);

        // Проверка результата
        assertNotNull(udr);
        assertEquals(VALID_MSISDN, udr.getMsisdn());
        assertNotNull(udr.getIncomingCall());
        assertNotNull(udr.getOutcomingCall());
    }

    /**
     * Тест для метода {@link UDRController#getUDR(String, String)}.
     * Проверяет обработку ошибки при неверном формате месяца.
     * Ожидается, что метод выбросит исключение {@link IllegalArgumentException}.
     */
    @Test
    void testGetUDR_InvalidMonthFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                udrController.getUDR(VALID_MSISDN, INVALID_MONTH)
        );
    }

    /**
     * Тест для метода {@link UDRController#getUDR(String, String)}.
     * Проверяет обработку ошибки при отсутствии данных.
     * Ожидается, что метод выбросит исключение {@link EntityNotFoundException}.
     */
    @Test
    void testGetUDR_NoDataFound() {
        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () ->
                udrController.getUDR(VALID_MSISDN, VALID_MONTH)
        );
    }
    /**
     * Тест для метода {@link UDRController#getAllUDRs(String)}.
     * Проверяет успешное получение UDR для всех абонентов.
     * Ожидается, что метод вернет Map с UDR для всех абонентов.
     */
    @Test
    void testGetAllUDRs_Success() {
        CDR cdr = CDR.builder()
                .callType("01")
                .msisdn(VALID_MSISDN)
                .otherMsisdn(OTHER_MSISDN)
                .callStartTime(LocalDateTime.now())
                .callEndTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(cdrRepository.findByCallStartTimeBetween(any(), any())).thenReturn(Collections.singletonList(cdr));

        // Вызов метода
        Map<String, UDR> udrMap = udrController.getAllUDRs(VALID_MONTH);

        // Проверка результата
        assertNotNull(udrMap);
        assertFalse(udrMap.isEmpty());
        assertTrue(udrMap.containsKey(VALID_MSISDN));
    }
}