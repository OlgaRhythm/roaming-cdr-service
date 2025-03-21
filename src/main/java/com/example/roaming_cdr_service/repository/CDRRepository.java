package com.example.roaming_cdr_service.repository;

import com.example.roaming_cdr_service.model.CDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для работы с базой данных
 */
@Repository
public interface CDRRepository extends JpaRepository<CDR, Long> {
    List<CDR> findByMsisdnAndCallStartTimeBetween(String msisdn, LocalDateTime start, LocalDateTime end);
}