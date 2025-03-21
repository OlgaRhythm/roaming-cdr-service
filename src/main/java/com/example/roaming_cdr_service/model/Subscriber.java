package com.example.roaming_cdr_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Абонент
 */
@Data
@Entity
@Table(name = "subscriber")
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String msisdn; // Номер абонента

    public Subscriber(String msisdn) {
        this.msisdn = msisdn;
    }
}