package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.CallDuration;
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
 * <p>
 * Тесты проверяют корректность работы методов контроллера, включая обработку успешных сценариев и ошибок.
 * </p>
 */
class UDRControllerTest {

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
     * <p>
     * Ожидается, что метод вернет корректный UDR объект с данными о звонках.
     * </p>
     */
    @Test
    void testGetUDR_Success() {
        // Подготовка данных
        String msisdn = "79991112233";
        String month = "2025-02";

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setMsisdn(msisdn);
        cdr.setOtherMsisdn("79992223344");
        cdr.setCallStartTime(LocalDateTime.now());
        cdr.setCallEndTime(LocalDateTime.now().plusMinutes(5));

        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.singletonList(cdr));
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        // Вызов метода
        UDR udr = udrController.getUDR(msisdn, month);

        // Проверка результата
        assertNotNull(udr);
        assertEquals(msisdn, udr.getMsisdn());
        assertNotNull(udr.getIncomingCall());
        assertNotNull(udr.getOutcomingCall());
    }

    /**
     * Тест для метода {@link UDRController#getUDR(String, String)}.
     * Проверяет обработку ошибки при неверном формате месяца.
     * <p>
     * Ожидается, что метод выбросит исключение {@link IllegalArgumentException}.
     * </p>
     */
    @Test
    void testGetUDR_InvalidMonthFormat() {
        // Подготовка данных
        String msisdn = "79991112233";
        String month = "invalid-month";

        // Вызов метода и проверка исключения
        assertThrows(IllegalArgumentException.class, () -> udrController.getUDR(msisdn, month));
    }

    /**
     * Тест для метода {@link UDRController#getUDR(String, String)}.
     * Проверяет обработку ошибки при отсутствии данных.
     * <p>
     * Ожидается, что метод выбросит исключение {@link EntityNotFoundException}.
     * </p>
     */
    @Test
    void testGetUDR_NoDataFound() {
        // Подготовка данных
        String msisdn = "79991112233";
        String month = "2025-02";

        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        // Вызов метода и проверка исключения
        assertThrows(EntityNotFoundException.class, () -> udrController.getUDR(msisdn, month));
    }

    /**
     * Тест для метода {@link UDRController#getAllUDRs(String)}.
     * Проверяет успешное получение UDR для всех абонентов.
     * <p>
     * Ожидается, что метод вернет Map с UDR для всех абонентов.
     * </p>
     */
    @Test
    void testGetAllUDRs_Success() {
        // Подготовка данных
        String month = "2025-02";

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setMsisdn("79991112233");
        cdr.setOtherMsisdn("79992223344");
        cdr.setCallStartTime(LocalDateTime.now());
        cdr.setCallEndTime(LocalDateTime.now().plusMinutes(5));

        when(cdrRepository.findByCallStartTimeBetween(any(), any())).thenReturn(Collections.singletonList(cdr));

        // Вызов метода
        Map<String, UDR> udrMap = udrController.getAllUDRs(month);

        // Проверка результата
        assertNotNull(udrMap);
        assertFalse(udrMap.isEmpty());
        assertTrue(udrMap.containsKey("79991112233"));
    }
}