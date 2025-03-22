package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.CallDuration;
import com.example.roaming_cdr_service.model.UDR;
import com.example.roaming_cdr_service.repository.CDRRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с UDR (Usage Data Report) отчётами.
 * <p>
 * Предоставляет REST API для получения UDR отчётов по звонкам абонентов.
 * </p>
 */
@RestController
@RequestMapping("/udr")
@Tag(name = "UDR API", description = "API для работы с UDR отчётами")
public class UDRController {

    @Autowired
    private CDRRepository cdrRepository;

    /**
     * Возвращает UDR отчёт для указанного абонента за заданный месяц или за весь период.
     *
     * @param msisdn Номер абонента.
     * @param month  Месяц в формате "yyyy-MM". Если не указан, возвращаются данные за весь период.
     * @return UDR отчёт для указанного абонента.
     */
    @Operation(
            summary = "Получить UDR для одного абонента",
            description = "Возвращает UDR для указанного абонента за указанный месяц или за весь период.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "400", description = "Неверный формат месяца"),
                    @ApiResponse(responseCode = "404", description = "Абонент не найден")
            }
    )
    @GetMapping("/{msisdn}")
    public UDR getUDR(
            @Parameter(description = "Номер абонента", example = "79991112233")
            @PathVariable String msisdn,

            @Parameter(description = "Месяц в формате yyyy-MM", example = "2025-02")
            @RequestParam(required = false) String month
    ) {
        LocalDateTime startDate = month != null ? LocalDateTime.parse(month + "-01T00:00:00") : LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = month != null ? startDate.plusMonths(1) : LocalDateTime.now();

        List<CDR> cdrsAsCaller = cdrRepository.findByMsisdnAndCallStartTimeBetween(msisdn, startDate, endDate);

        List<CDR> cdrsAsReceiver = cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(msisdn, startDate, endDate);

        List<CDR> allCdrs = new ArrayList<>();
        allCdrs.addAll(cdrsAsCaller);
        allCdrs.addAll(cdrsAsReceiver);

        UDR udr = new UDR();
        udr.setMsisdn(msisdn);
        udr.setIncomingCall(new CallDuration(allCdrs.stream()
                .filter(cdr -> cdr.getCallType().equals("02"))
                .mapToLong(cdr -> Duration.between(cdr.getCallStartTime(), cdr.getCallEndTime()).getSeconds())
                .sum()));
        udr.setOutcomingCall(new CallDuration(allCdrs.stream()
                .filter(cdr -> cdr.getCallType().equals("01"))
                .mapToLong(cdr -> Duration.between(cdr.getCallStartTime(), cdr.getCallEndTime()).getSeconds())
                .sum()));

        return udr;
    }

    /**
     * Возвращает UDR отчёты для всех абонентов за указанный месяц.
     *
     * @param month Месяц в формате "yyyy-MM".
     * @return Map, где ключ — номер абонента, значение — UDR отчёт.
     */
    @Operation(
            summary = "Получить UDR для всех абонентов",
            description = "Возвращает UDR для всех абонентов за указанный месяц.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "400", description = "Неверный формат месяца")
            }
    )
    @GetMapping("/all")
    public Map<String, UDR> getAllUDRs(
            @Parameter(description = "Месяц в формате yyyy-MM", example = "2025-02")
            @RequestParam String month
    ) {
        LocalDateTime startDate = LocalDateTime.parse(month + "-01T00:00:00");
        LocalDateTime endDate = startDate.plusMonths(1);

        List<CDR> allCdrs = cdrRepository.findByCallStartTimeBetween(startDate, endDate);

        Map<String, List<CDR>> cdrsByMsisdn = allCdrs.stream()
                .collect(Collectors.groupingBy(CDR::getMsisdn));

        Map<String, UDR> udrMap = new HashMap<>();
        for (Map.Entry<String, List<CDR>> entry : cdrsByMsisdn.entrySet()) {
            String msisdn = entry.getKey();
            List<CDR> cdrs = entry.getValue();

            UDR udr = new UDR();
            udr.setMsisdn(msisdn);
            udr.setIncomingCall(new CallDuration(cdrs.stream()
                    .filter(cdr -> cdr.getCallType().equals("02"))
                    .mapToLong(cdr -> Duration.between(cdr.getCallStartTime(), cdr.getCallEndTime()).getSeconds())
                    .sum()));
            udr.setOutcomingCall(new CallDuration(cdrs.stream()
                    .filter(cdr -> cdr.getCallType().equals("01"))
                    .mapToLong(cdr -> Duration.between(cdr.getCallStartTime(), cdr.getCallEndTime()).getSeconds())
                    .sum()));

            udrMap.put(msisdn, udr);
        }

        return udrMap;
    }
}