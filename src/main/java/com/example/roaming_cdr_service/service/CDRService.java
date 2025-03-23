package com.example.roaming_cdr_service.service;

import com.example.roaming_cdr_service.model.CDR;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с CDR (Call Data Record) записями.
 * Предоставляет метод для получения CDR-записей абонента за определённый период.
 */
public interface CDRService {

    /**
     * Получает список CDR-записей для указанного абонента за заданный временной диапазон.
     *
     * @param msisdn Номер абонента в формате строки (MSISDN).
     * @param start  Дата и время начала периода выборки.
     * @param end    Дата и время окончания периода выборки.
     * @return Список CDR-записей, соответствующих указанному абоненту и временному интервалу.
     */
    List<CDR> getCDRsForSubscriber(String msisdn, LocalDateTime start, LocalDateTime end);
}
