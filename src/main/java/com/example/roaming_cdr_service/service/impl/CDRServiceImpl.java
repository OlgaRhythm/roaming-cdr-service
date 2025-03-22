package com.example.roaming_cdr_service.service.impl;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.Subscriber;
import com.example.roaming_cdr_service.repository.CDRRepository;
import com.example.roaming_cdr_service.repository.SubscriberRepository;
import com.example.roaming_cdr_service.service.ICDRService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Сервис для генерации CDR (Call Data Record) записей.
 * <p>
 * Генерирует тестовые данные о звонках и сохраняет их в базу данных.
 * </p>
 */
@Service
public class CDRServiceImpl implements ICDRService {

    private static final int BATCH_SIZE = 100;
    private static final int CALL_INTERVAL_SECONDS = 3600; // Средний интервал между звонками — 1 час
    private static final int CALL_DURATION_SECONDS = 600; // Средняя длительность звонка — 10 минут
    private static final double LAMBDA_CALL_INTERVAL = 1.0 / CALL_INTERVAL_SECONDS;
    private static final double LAMBDA_CALL_DURATION = 1.0 / CALL_DURATION_SECONDS;
    private static final String ERROR_EMPTY_SUBSCRIBERS = "Список абонентов пуст, невозможно сгенерировать CDR!";
    private static final String ERROR_INSUFFICIENT_SUBSCRIBERS = "Недостаточно абонентов для генерации звонков (нужно минимум 2).";

    private final CDRRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;

    public CDRServiceImpl(CDRRepository cdrRepository, SubscriberRepository subscriberRepository) {
        this.cdrRepository = cdrRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public List<CDR> getCDRsForSubscriber(String msisdn, LocalDateTime start, LocalDateTime end) {
        List<CDR> cdrsAsCaller = cdrRepository.findByMsisdnAndCallStartTimeBetween(msisdn, start, end);
        List<CDR> cdrsAsReceiver = cdrRepository.findByOtherMsisdnAndCallStartTimeBetween(msisdn, start, end);

        List<CDR> allCdrs = new ArrayList<>(cdrsAsCaller);
        allCdrs.addAll(cdrsAsReceiver);
        return allCdrs;
    }

    @PostConstruct
    public void initDatabase() {
        if (subscriberRepository.count() == 0) { // Если таблица пустая, заполняем
            List<Subscriber> subscribers = List.of(
                    new Subscriber("79991112233"), new Subscriber("79992221122"),
                    new Subscriber("79993332211"), new Subscriber("79994443322"),
                    new Subscriber("79995554433"), new Subscriber("79996665544"),
                    new Subscriber("79997776655"), new Subscriber("79998887766"),
                    new Subscriber("79999998877"), new Subscriber("79990009988")
            );
            subscriberRepository.saveAll(subscribers);
        }
    }

    /**
     * Генерирует CDR записи за один год для всех абонентов.
     */
    @Transactional
    public void generateCDRs() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers.isEmpty()) {
            throw new IllegalStateException(ERROR_EMPTY_SUBSCRIBERS);        }

        if (subscribers.size() < 2) {
            throw new IllegalStateException(ERROR_INSUFFICIENT_SUBSCRIBERS);        }

        LocalDateTime startDate = LocalDateTime.now().minusYears(1); // Начало периода генерации (год назад)
        LocalDateTime endDate = LocalDateTime.now(); // Конец периода генерации (текущее время)

        // Отслеживание занятости абонентов (одновременно можно разговаривать только по 1 линии)
        Map<String, LocalDateTime> busySubscribers = new HashMap<>();
        List<CDR> batch = new ArrayList<>();

        for (LocalDateTime currentDate = startDate; currentDate.isBefore(endDate); currentDate = currentDate.plusSeconds(getExponentialRandom(1.0 / CALL_INTERVAL_SECONDS))) {
            long step = Math.max(1, getExponentialRandom(LAMBDA_CALL_INTERVAL)); // Гарантируем шаг > 0
            currentDate = currentDate.plusSeconds(step);

            CDR cdr = generateCDR(subscribers, busySubscribers, currentDate);
            if (cdr != null) {
                batch.add(cdr);
            }

            // Сохраняем пакет, если накопилось достаточно данных
            if (batch.size() >= BATCH_SIZE) {
                saveCDRBatch(batch);
            }
        }
        saveCDRBatch(batch);
    }

    /**
     * Генерирует CDR для случайных абонентов.
     */
    private CDR generateCDR(List<Subscriber> subscribers, Map<String, LocalDateTime> busySubscribers, LocalDateTime currentDate) {
        Subscriber caller = subscribers.get(ThreadLocalRandom.current().nextInt(subscribers.size()));
        Subscriber receiver;
        do {
            receiver = subscribers.get(ThreadLocalRandom.current().nextInt(subscribers.size()));
        } while (caller.getMsisdn().equals(receiver.getMsisdn()));

        if (isBusy(busySubscribers, caller.getMsisdn(), currentDate) ||
                isBusy(busySubscribers, receiver.getMsisdn(), currentDate)) {
            return null;
        }

        LocalDateTime callEndTime = currentDate.plusSeconds(getExponentialRandom(LAMBDA_CALL_DURATION));

        busySubscribers.put(caller.getMsisdn(), callEndTime);
        busySubscribers.put(receiver.getMsisdn(), callEndTime);

        CDR cdr = new CDR();
        cdr.setCallType(ThreadLocalRandom.current().nextBoolean() ? "01" : "02");
        cdr.setMsisdn(caller.getMsisdn());
        cdr.setOtherMsisdn(receiver.getMsisdn());
        cdr.setCallStartTime(currentDate);
        cdr.setCallEndTime(callEndTime);

        return cdr;
    }

    /**
     * Сохраняет CDR пакетами.
     */
    private void saveCDRBatch(List<CDR> batch) {
        if (!batch.isEmpty()) {
            cdrRepository.saveAll(batch);
            cdrRepository.flush();
            batch.clear();
        }
    }

    /**
     * Проверяет, занят ли абонент в указанное время
     * @param busySubscribers — мапа занятости абонентов
     * @param msisdn — номер абонента
     * @param currentDate — текущее время
     * @return true, если абонент занят, иначе false
     */
    private boolean isBusy(Map<String, LocalDateTime> busySubscribers, String msisdn, LocalDateTime currentDate) {
        LocalDateTime busyUntil = busySubscribers.get(msisdn);
        return busyUntil != null && currentDate.isBefore(busyUntil);
    }

    /**
     * Генерирует случайное число по экспоненциальному закону
     * @param lambda — интенсивность событий
     * @return случайное число
     */
    private long getExponentialRandom(double lambda) {
        return (long) (-Math.log(1 - ThreadLocalRandom.current().nextDouble()) / lambda);
    }
}