package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link UDR}.
 * Тесты проверяют корректность работы методов класса, включая геттеры, сеттеры, equals, hashCode и toString.
 */
class UDRTest {

    private static final String TEST_MSISDN = "79991112233";
    private static final long INCOMING_CALL_SECONDS = 3600L; // 1 час
    private static final long OUTCOMING_CALL_SECONDS = 1800L; // 30 минут
    private static final String EXPECTED_TO_STRING =
            "UDR(msisdn=79991112233, incomingCall=CallDuration(totalTime=01:00:00), " +
                    "outcomingCall=CallDuration(totalTime=00:30:00))";

    private CallDuration incomingCall;
    private CallDuration outcomingCall;

    @BeforeEach
    void setUp() {
        incomingCall = new CallDuration(INCOMING_CALL_SECONDS);
        outcomingCall = new CallDuration(OUTCOMING_CALL_SECONDS);
    }

    /**
     * Тест геттеров и сеттеров.
     */
    @Test
    void testGettersAndSetters() {
        UDR udr = createUDR();

        assertAll(
                () -> assertEquals(TEST_MSISDN, udr.getMsisdn()),
                () -> assertEquals(incomingCall, udr.getIncomingCall()),
                () -> assertEquals(outcomingCall, udr.getOutcomingCall())
        );
    }

    @Test
    void testEqualsAndHashCode() {
        UDR udr1 = createUDR();
        UDR udr2 = createUDR();

        assertAll(
                () -> assertEquals(udr1, udr2),
                () -> assertEquals(udr1.hashCode(), udr2.hashCode())
        );
    }

    /**
     * Тест {@code toString}.
     */
    @Test
    void testToString() {
        UDR udr = createUDR();
        assertEquals(EXPECTED_TO_STRING, udr.toString());
    }

    /**
     * Создает объект {@link UDR} с тестовыми данными.
     */
    private UDR createUDR() {
        return UDR.builder()
                .msisdn(TEST_MSISDN)
                .incomingCall(incomingCall)
                .outcomingCall(outcomingCall)
                .build();
    }
}