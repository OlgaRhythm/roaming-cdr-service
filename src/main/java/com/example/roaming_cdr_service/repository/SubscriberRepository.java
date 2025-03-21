package com.example.roaming_cdr_service.repository;

import com.example.roaming_cdr_service.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}