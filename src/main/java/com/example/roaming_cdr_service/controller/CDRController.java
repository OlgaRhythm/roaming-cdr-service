package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.service.CDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Обработка HTTP-запросов
 */
@RestController
@RequestMapping("/api/cdr")
public class CDRController {
    @Autowired
    private CDRService cdrService;

    @GetMapping("/generate")
    public String generateCDRs() {
        cdrService.generateCDRs();
        return "CDRs generated successfully!";
    }
}