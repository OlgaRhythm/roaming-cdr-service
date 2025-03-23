package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.service.ICDRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с CDR (Call Data Record) отчётами.
 * Предоставляет REST API для генерации CDR-отчётов в формате CSV.
 */
@RestController
@RequestMapping("/cdr")
public class CDRController {

    private final ICDRService cdrService;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String REPORTS_DIRECTORY = "reports/";
    private static final String ERROR_INVALID_DATE_FORMAT = "Неверный формат даты. Используйте %s.";
    private static final String ERROR_NO_DATA_FOUND = "Для абонента с номером %s не найдены записи за указанный период.";
    private static final String SUCCESS_REPORT_MESSAGE = "Отчет успешно создан. UUID: %s";
    private static final String ERROR_REPORT_CREATION = "Ошибка при создании отчета: %s";

    @Autowired
    public CDRController(ICDRService cdrService) {
        this.cdrService = cdrService;
    }

    /**
     * Генерирует CDR-отчёт для указанного абонента за заданный период времени.
     * Отчёт сохраняется в формате CSV в директорию {@code /reports}.
     *
     * @param msisdn    Номер абонента, для которого генерируется отчёт.
     * @param startDate Начальная дата периода в формате {@code yyyy-MM-dd'T'HH:mm:ss}.
     * @param endDate   Конечная дата периода в формате {@code yyyy-MM-dd'T'HH:mm:ss}.
     * @return Ответ с UUID сгенерированного отчёта или сообщением об ошибке.
     */
    @Operation(
            summary = "Сгенерировать CDR-отчет",
            description = "Генерирует CDR-отчет для указанного абонента за заданный период.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отчет успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
                    @ApiResponse(responseCode = "404", description = "Данные для абонента не найдены"),
                    @ApiResponse(responseCode = "500", description = "Ошибка при создании отчета")
            }
    )
    @PostMapping("/generate-report")
    public ResponseEntity<String> generateCDRReport(
            @Parameter(description = "Номер абонента", example = "79991112233")
            @RequestParam String msisdn,

            @Parameter(description = "Начальная дата в формате " + DATE_FORMAT, example = "2025-02-01T00:00:00")
            @RequestParam String startDate,

            @Parameter(description = "Конечная дата в формате " + DATE_FORMAT, example = "2025-02-28T23:59:59")
            @RequestParam String endDate
    ) {

        LocalDateTime start;
        LocalDateTime end;

        try {
            start = LocalDateTime.parse(startDate, DATE_TIME_FORMATTER);
            end = LocalDateTime.parse(endDate, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format(ERROR_INVALID_DATE_FORMAT, DATE_FORMAT));
        }

        // Получаем данные из БД
        List<CDR> cdrs = cdrService.getCDRsForSubscriber(msisdn, start, end);

        if (cdrs.isEmpty()) {
            throw new EntityNotFoundException(String.format(ERROR_NO_DATA_FOUND, msisdn));
        }

        return saveReportToFile(msisdn, cdrs);
    }

    /**
     * Сохраняет CDR-отчет в файл.
     *
     * @param msisdn    Номер абонента, для которого генерируется отчёт.
     * @param cdrs  Список CDR записей
     * @return Ответ с результатом операции
     */
    private ResponseEntity<String> saveReportToFile(String msisdn, List<CDR> cdrs) {

        // Генерируем уникальный UUID для имени файла
        String uuid = UUID.randomUUID().toString();
        String fileName = String.format("%s_%s.csv", msisdn, uuid);
        String filePath = Paths.get(REPORTS_DIRECTORY, fileName).toString();

        try {
            // Создаем директорию, если она не существует
            Files.createDirectories(Paths.get(REPORTS_DIRECTORY));

            // Записываем данные в CSV
            try (FileWriter writer = new FileWriter(filePath);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

                for (CDR cdr : cdrs) {
                    csvPrinter.printRecord(
                            cdr.getCallType(),
                            cdr.getMsisdn(),
                            cdr.getOtherMsisdn(),
                            cdr.getCallStartTime().format(DATE_TIME_FORMATTER),
                            cdr.getCallEndTime().format(DATE_TIME_FORMATTER)
                    );
                }
                csvPrinter.flush();
            }
            return ResponseEntity.ok(String.format(SUCCESS_REPORT_MESSAGE, uuid));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ERROR_REPORT_CREATION + e.getMessage());
        }
    }
}