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
 * <p>
 * Тесты проверяют корректность запуска приложения и выполнения метода {@link CommandLineRunner#run}.
 * </p>
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class) // Подключаем Mockito
class RoamingCdrServiceApplicationTests {

	@Mock
	private CDRServiceImpl cdrServiceImpl; // Используем @Mock вместо @MockBean

	@InjectMocks
	private RoamingCdrServiceApplication application; // Внедряем моки в тестируемый класс

	/**
	 * Тест для проверки загрузки контекста Spring.
	 * <p>
	 * Проверяет, что контекст приложения успешно инициализируется.
	 * </p>
	 */
	@Test
	void contextLoads() {
		ConfigurableApplicationContext context = SpringApplication.run(RoamingCdrServiceApplication.class);
		assertNotNull(context, "Контекст Spring не должен быть null");
		context.close(); // Закрываем контекст после завершения теста
	}

	/**
	 * Тест для метода {@link RoamingCdrServiceApplication#run(String...)}.
	 * <p>
	 * Проверяет, что метод {@link CDRServiceImpl#generateCDRs()} вызывается при запуске приложения.
	 * </p>
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
	 * <p>
	 * Проверяет обработку исключения, если {@link CDRServiceImpl#generateCDRs()} выбрасывает исключение.
	 * </p>
	 */
	@Test
	void testRun_Exception() throws Exception {
		// Настройка mock-объекта для выбрасывания исключения
		doThrow(new RuntimeException("Ошибка генерации CDR")).when(cdrServiceImpl).generateCDRs();

		// Вызов метода run и проверка исключения
		Exception exception = assertThrows(RuntimeException.class, () -> application.run());

		// Проверка сообщения об ошибке
		assertEquals("Ошибка генерации CDR", exception.getMessage());

		// Проверка, что метод generateCDRs был вызван
		verify(cdrServiceImpl, times(1)).generateCDRs();
	}
}