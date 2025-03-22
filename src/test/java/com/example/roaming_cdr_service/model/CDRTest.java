package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link CDR}.
 * <p>
 * Тесты проверяют корректность работы методов класса, включая геттеры, сеттеры, equals, hashCode и toString.
 * </p>
 */
class CDRTest {

    @Test
    void testGettersAndSetters() {
        CDR cdr = new CDR();

        // Устанавливаем значения
        cdr.setId(1L);
        cdr.setCallType("01");
        cdr.setMsisdn("79991112233");
        cdr.setOtherMsisdn("79992223344");
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(5);
        cdr.setCallStartTime(startTime);
        cdr.setCallEndTime(endTime);

        // Проверяем значения
        assertEquals(1L, cdr.getId());
        assertEquals("01", cdr.getCallType());
        assertEquals("79991112233", cdr.getMsisdn());
        assertEquals("79992223344", cdr.getOtherMsisdn());
        assertEquals(startTime, cdr.getCallStartTime());
        assertEquals(endTime, cdr.getCallEndTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(5);

        CDR cdr1 = new CDR();
        cdr1.setId(1L);
        cdr1.setCallType("01");
        cdr1.setMsisdn("79991112233");
        cdr1.setOtherMsisdn("79992223344");
        cdr1.setCallStartTime(startTime);
        cdr1.setCallEndTime(endTime);

        CDR cdr2 = new CDR();
        cdr2.setId(1L);
        cdr2.setCallType("01");
        cdr2.setMsisdn("79991112233");
        cdr2.setOtherMsisdn("79992223344");
        cdr2.setCallStartTime(startTime);
        cdr2.setCallEndTime(endTime);

        // Проверка equals
        assertEquals(cdr1, cdr2);

        // Проверка hashCode
        assertEquals(cdr1.hashCode(), cdr2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(5);

        CDR cdr = new CDR();
        cdr.setId(1L);
        cdr.setCallType("01");
        cdr.setMsisdn("79991112233");
        cdr.setOtherMsisdn("79992223344");
        cdr.setCallStartTime(startTime);
        cdr.setCallEndTime(endTime);

        // Проверка toString
        String expectedToString = "CDR(id=1, callType=01, msisdn=79991112233, otherMsisdn=79992223344, " +
                "callStartTime=" + startTime + ", callEndTime=" + endTime + ")";
        assertEquals(expectedToString, cdr.toString());
    }
}