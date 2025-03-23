package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link CallDuration}.
 */
class CallDurationTest {

    private static final long TOTAL_SECONDS = 3661; // 1 час, 1 минута, 1 секунда
    private static final String EXPECTED_FORMATTED_TIME = "01:01:01";

    /**
     * Тест для метода {@link CallDuration#formatDuration(long)}.
     * Проверяет корректное форматирование времени.
     */
    @Test
    void testFormatDuration() {
        // Вызов метода
        CallDuration callDuration = new CallDuration(TOTAL_SECONDS);

        // Проверка результата
        assertEquals(EXPECTED_FORMATTED_TIME, callDuration.getTotalTime());
    }

}