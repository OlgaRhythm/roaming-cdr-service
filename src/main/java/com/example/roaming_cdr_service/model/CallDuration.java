package com.example.roaming_cdr_service.model;

import lombok.Data;

/**
 * Класс для представления длительности звонков.
 * Содержит общее время звонков в формате "HH:mm:ss".
 */
@Data
public class CallDuration {

    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final String TIME_FORMAT = "%02d:%02d:%02d";

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
    public String formatDuration(long totalSeconds) {
        long hours = totalSeconds / SECONDS_IN_HOUR;
        long minutes = (totalSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;
        return String.format(TIME_FORMAT, hours, minutes, seconds);    }
}