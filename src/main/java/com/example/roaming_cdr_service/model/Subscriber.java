package com.example.roaming_cdr_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для представления абонента.
 * Содержит информацию о номере абонента (MSISDN).
 */
@Data
@Entity
@Table(name = "subscriber")
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {

    /**
     * Уникальный идентификатор абонента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер абонента (MSISDN).
     */
    private String msisdn;

    /**
     * Конструктор для создания абонента с указанным номером.
     *
     * @param msisdn Номер абонента.
     */
    public Subscriber(String msisdn) {
        this.msisdn = msisdn;
    }

}