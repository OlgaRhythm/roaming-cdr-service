package com.example.roaming_cdr_service.model;

import lombok.Data;

@Data
public class CallDuration {
    private String totalTime;

    public CallDuration(long totalSeconds) {
        this.totalTime = formatDuration(totalSeconds);
    }

    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}