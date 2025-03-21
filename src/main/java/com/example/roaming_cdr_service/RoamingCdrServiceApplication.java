package com.example.roaming_cdr_service;

import com.example.roaming_cdr_service.model.Subscriber;
import com.example.roaming_cdr_service.service.CDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoamingCdrServiceApplication implements CommandLineRunner {
	@Autowired
	private CDRService cdrService;

	public static void main(String[] args) {
		SpringApplication.run(RoamingCdrServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		cdrService.generateCDRs();
	}
}
