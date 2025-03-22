package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.repository.CDRRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса {@link CDRController}.
 * <p>
 * Тесты проверяют корректность работы методов контроллера, включая обработку успешных сценариев и ошибок.
 * </p>
 */
class CDRControllerTest {

    @Mock
    private CDRRepository cdrRepository;

    @InjectMocks
    private CDRController cdrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет успешную генерацию отчёта.
     * <p>
     * Ожидается, что метод вернет статус 200 и сообщение об успешном создании отчёта.
     * </p>
     */
    @Test
    void testGenerateCDRReport_Success() {
        // Подготовка данных
        String msisdn = "79991112233";
        String startDate = "2025-02-01T00:00:00";
        String endDate = "2025-02-28T23:59:59";

        CDR cdr = new CDR();
        cdr.setCallType("01");
        cdr.setMsisdn(msisdn);
        cdr.setOtherMsisdn("79992223344");
        cdr.setCallStartTime(LocalDateTime.now());
        cdr.setCallEndTime(LocalDateTime.now().plusMinutes(5));

        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.singletonList(cdr));
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.singletonList(cdr));

        // Вызов метода
        ResponseEntity<String> response = cdrController.generateCDRReport(msisdn, startDate, endDate);

        // Проверка результата
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Отчет успешно создан"));
    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет обработку ошибки при неверном формате даты.
     * <p>
     * Ожидается, что метод вернет статус 400 и сообщение об ошибке.
     * </p>
     */
    @Test
    void testGenerateCDRReport_InvalidDateFormat() {
        // Подготовка данных
        String msisdn = "79991112233";
        String startDate = "invalid-date";
        String endDate = "2025-02-28T23:59:59";

        // Вызов метода и проверка исключения
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cdrController.generateCDRReport(msisdn, startDate, endDate);
        });

        // Проверка сообщения об ошибке
        assertEquals("Неверный формат даты. Используйте yyyy-MM-dd'T'HH:mm:ss.", exception.getMessage());    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет обработку ошибки при отсутствии данных в БД.
     * <p>
     * Ожидается, что метод вернет статус 404, так как отсутствие данных является ошибкой.
     * </p>
     */
    @Test
    void testGenerateCDRReport_NoDataFound() {
        // Подготовка данных
        String msisdn = "79991112233";
        String startDate = "2025-02-01T00:00:00";
        String endDate = "2025-02-28T23:59:59";

        when(cdrRepository.findByMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());
        when(cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        // Вызов метода и проверка исключения
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            cdrController.generateCDRReport(msisdn, startDate, endDate);
        });

        // Проверка сообщения об ошибке
        assertTrue(exception.getMessage().contains("не найдены записи"));
    }
}