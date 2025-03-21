package com.example.roaming_cdr_service.model;

import lombok.Data;

/**
 * Класс для предоставления UDR отчётов
 */
@Data
public class UDR {
    private String msisdn;
    private String incomingCallTotalTime;
    private String outcomingCallTotalTime;
}