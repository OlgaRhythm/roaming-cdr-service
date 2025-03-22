package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.repository.CDRRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с CDR (Call Data Record) отчётами.
 * <p>
 * Предоставляет REST API для генерации CDR-отчётов в формате CSV.
 * </p>
 */
@RestController
@RequestMapping("/cdr")
public class CDRController {
    @Autowired
    private CDRRepository cdrRepository;

    /**
     * Генерирует CDR-отчёт для указанного абонента за заданный период времени.
     * <p>
     * Отчёт сохраняется в формате CSV в директорию {@code /reports}.
     * </p>
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
                    @ApiResponse(responseCode = "500", description = "Ошибка при создании отчета")
            }
    )
    @PostMapping("/generate-report")
    public ResponseEntity<String> generateCDRReport(
            @Parameter(description = "Номер абонента", example = "79991112233")
            @RequestParam String msisdn,

            @Parameter(description = "Начальная дата в формате yyyy-MM-dd'T'HH:mm:ss", example = "2025-02-01T00:00:00")
            @RequestParam String startDate,

            @Parameter(description = "Конечная дата в формате yyyy-MM-dd'T'HH:mm:ss", example = "2025-02-28T23:59:59")
            @RequestParam String endDate
    ) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            // Получаем данные из БД
            List<CDR> cdrsAsCaller = cdrRepository.findByMsisdnAndCallStartTimeBetween(msisdn, start, end);
            List<CDR> cdrsAsReceiver = cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(msisdn, start, end);

            // Объединяем результаты
            List<CDR> cdrs = new ArrayList<>();
            cdrs.addAll(cdrsAsCaller);
            cdrs.addAll(cdrsAsReceiver);

            // Генерируем уникальный UUID для имени файла
            String uuid = UUID.randomUUID().toString();
            String fileName = msisdn + "_" + uuid + ".csv";
            String filePath = "reports/" + fileName;

            // Создаем директорию, если она не существует
            Files.createDirectories(Paths.get("reports"));

            // Записываем данные в CSV
            try (FileWriter writer = new FileWriter(filePath);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

                // Заголовки CSV (опционально)
//                csvPrinter.printRecord("callType", "msisdn", "otherMsisdn", "callStartTime", "callEndTime");

                // Данные
                for (CDR cdr : cdrs) {
                    csvPrinter.printRecord(
                            cdr.getCallType(),
                            cdr.getMsisdn(),
                            cdr.getOtherMsisdn(),
                            cdr.getCallStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), // Форматируем дату
                            cdr.getCallEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // Форматируем дату
                    );
                }

                csvPrinter.flush();
            }

            // Возвращаем UUID и статус
            return ResponseEntity.ok("Отчет успешно создан. UUID: " + uuid);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при создании отчета: " + e.getMessage());
        }
    }


}