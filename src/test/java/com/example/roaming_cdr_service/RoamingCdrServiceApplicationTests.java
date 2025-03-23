package com.example.roaming_cdr_service;

import com.example.roaming_cdr_service.service.impl.CDRServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link RoamingCdrServiceApplication}.
 * Тесты проверяют корректность запуска приложения и выполнения метода {@link CommandLineRunner#run}.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class) // Подключаем Mockito
class RoamingCdrServiceApplicationTests {

	private static final String ERROR_GENERATING_CDR = "Ошибка генерации CDR";

	@Mock
	private CDRServiceImpl cdrServiceImpl; // Используем @Mock вместо @MockBean

	@InjectMocks
	private RoamingCdrServiceApplication application; // Внедряем моки в тестируемый класс

	/**
	 * Тест для проверки загрузки контекста Spring.
	 * Проверяет, что контекст приложения успешно инициализируется.
	 */
	@Test
	void contextLoads() {
		try (ConfigurableApplicationContext context = SpringApplication.run(RoamingCdrServiceApplication.class)) {
			assertNotNull(context, "Контекст Spring не должен быть null");
		}	}

	/**
	 * Тест для метода {@link RoamingCdrServiceApplication#run(String...)}.
	 * Проверяет, что метод {@link CDRServiceImpl#generateCDRs()} вызывается при запуске приложения.
	 */
	@Test
	void testRun() throws Exception {
		// Вызов метода run
		application.run();

		// Проверка, что метод generateCDRs был вызван
		verify(cdrServiceImpl, times(1)).generateCDRs();
	}

	/**
	 * Тест для метода {@link RoamingCdrServiceApplication#run(String...)}.
	 * Проверяет обработку исключения, если {@link CDRServiceImpl#generateCDRs()} выбрасывает исключение.
	 */
	@Test
	void testRun_Exception() throws Exception {
		// Настройка mock-объекта для выбрасывания исключения
		doThrow(new RuntimeException(ERROR_GENERATING_CDR)).when(cdrServiceImpl).generateCDRs();

		// Вызов метода run и проверка исключения
		Exception exception = assertThrows(RuntimeException.class, () -> application.run());

		// Проверка сообщения об ошибке
		assertEquals(ERROR_GENERATING_CDR, exception.getMessage());

		// Проверка, что метод generateCDRs был вызван
		verify(cdrServiceImpl, times(1)).generateCDRs();
	}
}