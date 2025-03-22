package com.example.roaming_cdr_service.model;

import lombok.Data;

/**
 * Класс для представления UDR (Usage Data Report) отчётов.
 * <p>
 * UDR отчёты содержат информацию об использовании данных абонентом,
 * включая общее время входящих и исходящих звонков.
 * </p>
 */
@Data
public class UDR {
    /**
     * Номер абонента (MSISDN).
     */
    private String msisdn;

    /**
     * Информация о входящих звонках.
     */
    private CallDuration incomingCall;

    /**
     * Информация об исходящих звонках.
     */
    private CallDuration outcomingCall;
}

