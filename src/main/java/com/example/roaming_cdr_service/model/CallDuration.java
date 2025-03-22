package com.example.roaming_cdr_service.model;

import lombok.Data;

/**
 * Класс для представления длительности звонков.
 * <p>
 * Содержит общее время звонков в формате "HH:mm:ss".
 * </p>
 */
@Data
public class CallDuration {
    /**
     * Общее время звонков в формате "HH:mm:ss".
     */
    private String totalTime;

    /**
     * Конструктор, который принимает общее время в секундах и преобразует его в формат "HH:mm:ss".
     *
     * @param totalSeconds Общее время звонков в секундах.
     */
    public CallDuration(long totalSeconds) {
        this.totalTime = formatDuration(totalSeconds);
    }

    /**
     * Преобразует общее время в секундах в формат "HH:mm:ss".
     *
     * @param totalSeconds Общее время звонков в секундах.
     * @return Строка в формате "HH:mm:ss".
     */
    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}