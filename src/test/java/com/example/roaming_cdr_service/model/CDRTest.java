package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link CDR}.
 * Тесты проверяют корректность работы методов класса, включая геттеры, сеттеры, equals, hashCode и toString.
 */
class CDRTest {

    private static final Long TEST_ID = 1L;
    private static final String CALL_TYPE = "01";
    private static final String MSISDN = "79991112233";
    private static final String OTHER_MSISDN = "79992223344";
    private static final long CALL_DURATION_MINUTES = 5;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now();
        endTime = startTime.plusMinutes(CALL_DURATION_MINUTES);
    }

    @Test
    void testGettersAndSetters() {
        CDR cdr = createCDR();

        assertAll(
                () -> assertEquals(TEST_ID, cdr.getId()),
                () -> assertEquals(CALL_TYPE, cdr.getCallType()),
                () -> assertEquals(MSISDN, cdr.getMsisdn()),
                () -> assertEquals(OTHER_MSISDN, cdr.getOtherMsisdn()),
                () -> assertEquals(startTime, cdr.getCallStartTime()),
                () -> assertEquals(endTime, cdr.getCallEndTime())
        );
    }

    /**
     * Тест {@code equals} и {@code hashCode}.
     */
    @Test
    void testEqualsAndHashCode() {
        CDR cdr1 = createCDR();
        CDR cdr2 = createCDR();

        assertAll(
                () -> assertEquals(cdr1, cdr2),
                () -> assertEquals(cdr1.hashCode(), cdr2.hashCode())
        );
    }

    /**
     * Тест {@code toString}.
     */
    @Test
    void testToString() {
        CDR cdr = createCDR();

        String expectedToString = String.format(
                "CDR(id=%d, callType=%s, msisdn=%s, otherMsisdn=%s, callStartTime=%s, callEndTime=%s)",
                TEST_ID, CALL_TYPE, MSISDN, OTHER_MSISDN, startTime, endTime
        );

        assertEquals(expectedToString, cdr.toString());
    }

    /**
     * Создает объект {@link CDR} с тестовыми данными.
     */
    private CDR createCDR() {
        return CDR.builder()
                .id(TEST_ID)
                .callType(CALL_TYPE)
                .msisdn(MSISDN)
                .otherMsisdn(OTHER_MSISDN)
                .callStartTime(startTime)
                .callEndTime(endTime)
                .build();
    }
}