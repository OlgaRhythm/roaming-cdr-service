package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link UDR}.
 * <p>
 * Тесты проверяют корректность работы методов класса, включая геттеры, сеттеры, equals, hashCode и toString.
 * </p>
 */
class UDRTest {

    @Test
    void testGettersAndSetters() {
        UDR udr = new UDR();

        // Устанавливаем значения
        udr.setMsisdn("79991112233");
        CallDuration incomingCall = new CallDuration(3600L); // 1 час
        CallDuration outcomingCall = new CallDuration(1800L); // 30 минут
        udr.setIncomingCall(incomingCall);
        udr.setOutcomingCall(outcomingCall);

        // Проверяем значения
        assertEquals("79991112233", udr.getMsisdn());
        assertEquals(incomingCall, udr.getIncomingCall());
        assertEquals(outcomingCall, udr.getOutcomingCall());
    }

    @Test
    void testEqualsAndHashCode() {
        UDR udr1 = new UDR();
        udr1.setMsisdn("79991112233");
        udr1.setIncomingCall(new CallDuration(3600L));
        udr1.setOutcomingCall(new CallDuration(1800L));

        UDR udr2 = new UDR();
        udr2.setMsisdn("79991112233");
        udr2.setIncomingCall(new CallDuration(3600L));
        udr2.setOutcomingCall(new CallDuration(1800L));

        // Проверка equals
        assertEquals(udr1, udr2);

        // Проверка hashCode
        assertEquals(udr1.hashCode(), udr2.hashCode());
    }

    @Test
    void testToString() {
        UDR udr = new UDR();
        udr.setMsisdn("79991112233");
        udr.setIncomingCall(new CallDuration(3600L)); // 1 час
        udr.setOutcomingCall(new CallDuration(1800L)); // 30 минут

        // Проверка toString
        String expectedToString = "UDR(msisdn=79991112233, incomingCall=CallDuration(totalTime=01:00:00), " +
                "outcomingCall=CallDuration(totalTime=00:30:00))";
        assertEquals(expectedToString, udr.toString());
    }
}