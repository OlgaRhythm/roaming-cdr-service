package com.example.roaming_cdr_service;

import com.example.roaming_cdr_service.service.impl.CDRServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения Roaming CDR Service.
 * Этот класс является точкой входа в приложение. Он запускает Spring Boot приложение
 * и инициализирует генерацию CDR записей с помощью {@link CDRServiceImpl}.
 */
@SpringBootApplication
public class RoamingCdrServiceApplication implements CommandLineRunner {

	@Autowired
	private CDRServiceImpl cdrServiceImpl;

	/**
	 * Точка входа в приложение.
	 *
	 * @param args Аргументы командной строки.
	 */
	public static void main(String[] args) {
		SpringApplication.run(RoamingCdrServiceApplication.class, args);
	}

	/**
	 * Метод, который выполняется после запуска приложения.
	 * Инициализирует генерацию CDR записей с помощью {@link CDRServiceImpl}.
	 *
	 * @param args Аргументы командной строки.
	 * @throws Exception Если произошла ошибка при выполнении.
	 */
	@Override
	public void run(String... args) throws Exception {
		cdrServiceImpl.generateCDRs();
	}
}
