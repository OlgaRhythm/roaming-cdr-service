package com.example.roaming_cdr_service.repository;

import com.example.roaming_cdr_service.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностью {@link Subscriber}.
 * <p>
 * Предоставляет методы для выполнения операций CRUD (Create, Read, Update, Delete) с абонентами.
 * </p>
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}