package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link CallDuration}.
 */
class CallDurationTest {

    /**
     * Тест для метода {@link CallDuration#formatDuration(long)}.
     * Проверяет корректное форматирование времени.
     */
    @Test
    void testFormatDuration() {
        // Подготовка данных
        long totalSeconds = 3661; // 1 час, 1 минута, 1 секунда

        // Вызов метода
        CallDuration callDuration = new CallDuration(totalSeconds);

        // Проверка результата
        assertEquals("01:01:01", callDuration.getTotalTime());
    }

}