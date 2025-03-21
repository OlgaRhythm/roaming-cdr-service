package com.example.roaming_cdr_service.service;

import com.example.roaming_cdr_service.model.CDR;
import com.example.roaming_cdr_service.model.Subscriber;
import com.example.roaming_cdr_service.repository.CDRRepository;
import com.example.roaming_cdr_service.repository.SubscriberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации CDR записей
 */
@Service
public class CDRService {
    @Autowired
    private CDRRepository cdrRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @PostConstruct
    public void initDatabase() {
        if (subscriberRepository.count() == 0) { // Если таблица пустая, заполняем
            List<Subscriber> subscribers = List.of(
                    new Subscriber("79991112233"),
                    new Subscriber("79992221122"),
                    new Subscriber("79993332211"),
                    new Subscriber("79994443322"),
                    new Subscriber("79995554433"),
                    new Subscriber("79996665544"),
                    new Subscriber("79997776655"),
                    new Subscriber("79998887766"),
                    new Subscriber("79999998877"),
                    new Subscriber("79990009988")
            );
            subscriberRepository.saveAll(subscribers);
        }
    }

    /**
     * Генерирует случайные CDR записи и сохраняет их в базу данных
     */
    @Transactional
    public void generateCDRs() {
        List<Subscriber> subscribers = subscriberRepository.findAll(); // Получаем всех абонентов

        LocalDateTime startDate = LocalDateTime.now().minusYears(1); // Начало периода генерации (год назад)
        LocalDateTime endDate = LocalDateTime.now(); // Конец периода генерации (текущее время)

        double lambdaCallInterval = 1.0 / 3600; // Средний интервал между звонками — 1 час
        double lambdaCallDuration = 1.0 / 600; // Средняя длительность звонка — 10 минут

        // Отслеживание занятости абонентов (одновременно можно разговаривать только по 1 линии)
        Map<String, LocalDateTime> busySubscribers = new HashMap<>();

        // Пакетное сохранение CDR
        int batchSize = 100; // Размер пакета
        int count = 0;

        for (LocalDateTime currentDate = startDate; currentDate.isBefore(endDate); currentDate = currentDate.plusSeconds(getExponentialRandom(lambdaCallInterval))) {
            // Выбираем случайных абонентов
            if (subscribers.isEmpty()) {
                throw new IllegalStateException("Список абонентов пуст, невозможно сгенерировать CDR!");
            }
            Subscriber caller = subscribers.get(ThreadLocalRandom.current().nextInt(subscribers.size()));
            Subscriber receiver = subscribers.get(ThreadLocalRandom.current().nextInt(subscribers.size()));

            // Проверяем, что абоненты не заняты
            if (isBusy(busySubscribers, caller.getMsisdn(), currentDate) || isBusy(busySubscribers, receiver.getMsisdn(), currentDate)) {
                continue; // Пропускаем этот звонок
            }

            // Генерируем CDR
            CDR cdr = new CDR();
            cdr.setCallType(ThreadLocalRandom.current().nextBoolean() ? "01" : "02"); // Случайный тип звонка
            cdr.setMsisdn(caller.getMsisdn()); // Номер абонента
            cdr.setOtherMsisdn(receiver.getMsisdn()); // Номер другого абонента
            cdr.setCallStartTime(currentDate);

            // Устанавливаем время окончания звонка
            LocalDateTime callEndTime = currentDate.plusSeconds(getExponentialRandom(lambdaCallDuration));
            cdr.setCallEndTime(callEndTime);

            // Сохраняем запись в базу данных (пакетно)
            cdrRepository.save(cdr);
            count++;

            // Обновляем мапу занятости
            busySubscribers.put(caller.getMsisdn(), callEndTime);
            busySubscribers.put(receiver.getMsisdn(), callEndTime);

            // Пакетная фиксация
            if (count % batchSize == 0) {
                cdrRepository.flush();
            }
        }

        // Фиксация оставшихся записей
        if (count % batchSize != 0) {
            cdrRepository.flush();
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