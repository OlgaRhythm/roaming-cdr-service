package com.example.roaming_cdr_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Класс для CDR записей
 */

@Data
@Entity
@Table(name = "cdr")
public class CDR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callType; // 01 - исходящий, 02 - входящий
    private String msisdn; // Номер абонента, инициирующего звонок
    private String otherMsisdn; // Номер абонента, принимающего звонок
    private LocalDateTime callStartTime; // Дата и время начала звонка (ISO 8601)
    private LocalDateTime callEndTime; // Дата и время окончания звонка (ISO 8601)
}