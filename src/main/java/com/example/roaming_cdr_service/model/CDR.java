package com.example.roaming_cdr_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Класс для представления CDR (Call Data Record) записей.
 * <p>
 * CDR записи содержат информацию о звонках, включая тип звонка, номера абонентов,
 * а также время начала и окончания звонка.
 * </p>
 */
@Data
@Entity
@Table(name = "cdr")
public class CDR {
    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип звонка:
     * <ul>
     *   <li>"01" — исходящий звонок.</li>
     *   <li>"02" — входящий звонок.</li>
     * </ul>
     */
    private String callType;

    /**
     * Номер абонента, инициирующего звонок.
     */
    private String msisdn;

    /**
     * Номер абонента, принимающего звонок.
     */
    private String otherMsisdn;

    /**
     * Дата и время начала звонка в формате ISO 8601.
     */
    private LocalDateTime callStartTime;

    /**
     * Дата и время окончания звонка в формате ISO 8601.
     */
    private LocalDateTime callEndTime;
}