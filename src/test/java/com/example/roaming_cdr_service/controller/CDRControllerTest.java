package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.service.CDRService;
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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса {@link CDRController}.
 * Тесты проверяют корректность работы методов контроллера, включая обработку успешных сценариев и ошибок.
 */
class CDRControllerTest {

    private static final String VALID_MSISDN = "79991112233";
    private static final String OTHER_MSISDN = "79992223344";
    private static final String START_DATE = "2025-02-01T00:00:00";
    private static final String END_DATE = "2025-02-28T23:59:59";
    private static final String INVALID_DATE = "invalid-date";
    private static final String DATE_FORMAT_ERROR = "Неверный формат даты. Используйте yyyy-MM-dd'T'HH:mm:ss.";
    private static final String REPORT_SUCCESS_MESSAGE = "Отчет успешно создан";
    private static final String NO_DATA_ERROR_MESSAGE = "не найдены записи";


    @Mock
    private CDRService cdrService;

    @InjectMocks
    private CDRController cdrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет успешную генерацию отчёта.
     * Ожидается, что метод вернет статус 200 и сообщение об успешном создании отчёта.
     */
    @Test
    void testGenerateCDRReport_Success() {
        // Подготовка данных
        LocalDateTime start = LocalDateTime.parse(START_DATE);
        LocalDateTime end = LocalDateTime.parse(END_DATE);

        CDR cdr = CDR.builder()
                .callType("01")
                .msisdn(VALID_MSISDN)
                .otherMsisdn(OTHER_MSISDN)
                .callStartTime(LocalDateTime.now())
                .callEndTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(cdrService.getCDRsForSubscriber(eq(VALID_MSISDN), any(), any())).thenReturn(List.of(cdr));

        // Вызов метода
        ResponseEntity<String> response = cdrController.generateCDRReport(VALID_MSISDN, START_DATE, END_DATE);

        // Проверка результата
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Отчет успешно создан"));
    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет обработку ошибки при неверном формате даты.
     * Ожидается, что метод вернет статус 400 и сообщение об ошибке.
     */
    @Test
    void testGenerateCDRReport_InvalidDateFormat() {

        // Вызов метода и проверка исключения
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                cdrController.generateCDRReport(VALID_MSISDN, INVALID_DATE, END_DATE)
        );

        // Проверка сообщения об ошибке
        assertEquals(DATE_FORMAT_ERROR, exception.getMessage());
    }

    /**
     * Тест для метода {@link CDRController#generateCDRReport(String, String, String)}.
     * Проверяет обработку ошибки при отсутствии данных в БД.
     * Ожидается, что метод вернет статус 404, так как отсутствие данных является ошибкой.
     */
    @Test
    void testGenerateCDRReport_NoDataFound() {

        when(cdrService.getCDRsForSubscriber(eq(VALID_MSISDN), any(), any())).thenReturn(Collections.emptyList());

        // Вызов метода и проверка исключения
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                cdrController.generateCDRReport(VALID_MSISDN, START_DATE, END_DATE)
        );

        // Проверка сообщения об ошибке
        assertTrue(exception.getMessage().contains(NO_DATA_ERROR_MESSAGE));
    }
}