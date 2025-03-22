package com.example.roaming_cdr_service.service;

import com.example.roaming_cdr_service.model.CDR;

import java.time.LocalDateTime;
import java.util.List;

public interface ICDRService {
    List<CDR> getCDRsForSubscriber(String msisdn, LocalDateTime start, LocalDateTime end);
}
