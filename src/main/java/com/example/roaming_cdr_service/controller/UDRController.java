package com.example.roaming_cdr_service.controller;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.CallDuration;
import com.example.roaming_cdr_service.model.UDR;
import com.example.roaming_cdr_service.repository.CDRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/udr")
public class UDRController {

    @Autowired
    private CDRRepository cdrRepository;

    /**
     * Получение UDR для одного абонента.
     *
     * @param msisdn Номер абонента.
     * @param month  Месяц в формате "yyyy-MM". Если не указан, возвращаются данные за весь период.
     * @return UDR для указанного абонента.
     */
    @GetMapping("/{msisdn}")
    public UDR getUDR(@PathVariable String msisdn, @RequestParam(required = false) String month) {
        LocalDateTime startDate = month != null ? LocalDateTime.parse(month + "-01T00:00:00") : LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = month != null ? startDate.plusMonths(1) : LocalDateTime.now();

        // Получаем записи, где абонент был инициатором звонка
        List<CDR> cdrsAsCaller = cdrRepository.findByMsisdnAndCallStartTimeBetween(msisdn, startDate, endDate);

        // Получаем записи, где абонент был получателем звонка
        List<CDR> cdrsAsReceiver = cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(msisdn, startDate, endDate);

        // Объединяем результаты
        List<CDR> allCdrs = new ArrayList<>();
        allCdrs.addAll(cdrsAsCaller);
        allCdrs.addAll(cdrsAsReceiver);

        // Создаем UDR
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
     * Получение UDR для всех абонентов за указанный месяц.
     *
     * @param month Месяц в формате "yyyy-MM".
     * @return Map, где ключ - номер абонента, значение - UDR.
     */
    @GetMapping("/all")
    public Map<String, UDR> getAllUDRs(@RequestParam String month) {
        LocalDateTime startDate = LocalDateTime.parse(month + "-01T00:00:00");
        LocalDateTime endDate = startDate.plusMonths(1);

        // Получаем все записи за указанный период
        List<CDR> allCdrs = cdrRepository.findByCallStartTimeBetween(startDate, endDate);

        // Группируем записи по абонентам
        Map<String, List<CDR>> cdrsByMsisdn = allCdrs.stream()
                .collect(Collectors.groupingBy(CDR::getMsisdn));

        // Создаем UDR для каждого абонента
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