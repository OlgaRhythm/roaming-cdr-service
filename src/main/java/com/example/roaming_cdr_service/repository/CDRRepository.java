package com.example.roaming_cdr_service.repository;

import com.example.roaming_cdr_service.model.CDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с CDR (Call Data Record) записями.
 * <p>
 * Предоставляет методы для поиска записей о звонках в базе данных.
 * </p>
 */
@Repository
public interface CDRRepository extends JpaRepository<CDR, Long> {
    /**
     * Находит все CDR записи для указанного абонента (msisdn) в заданном временном диапазоне.
     *
     * @param msisdn Номер абонента.
     * @param start  Начальная дата диапазона.
     * @param end    Конечная дата диапазона.
     * @return Список CDR записей.
     */
    List<CDR> findByMsisdnAndCallStartTimeBetween(String msisdn, LocalDateTime start, LocalDateTime end);

    /**
     * Находит все CDR записи, где указанный абонент (otherMsisdn) был получателем звонка в заданном временном диапазоне.
     *
     * @param otherMsisdn Номер абонента-получателя.
     * @param start       Начальная дата диапазона.
     * @param end         Конечная дата диапазона.
     * @return Список CDR записей.
     */
    List<CDR> findByOtherMsisdnAndCallStartTimeBetween(String otherMsisdn, LocalDateTime start, LocalDateTime end);

    /**
     * Находит все CDR записи в заданном временном диапазоне.
     *
     * @param start Начальная дата диапазона.
     * @param end   Конечная дата диапазона.
     * @return Список CDR записей.
     */
    List<CDR> findByCallStartTimeBetween(LocalDateTime start, LocalDateTime end);
}