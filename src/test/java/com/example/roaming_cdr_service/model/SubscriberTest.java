package com.example.roaming_cdr_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса {@link Subscriber}.
 * <p>
 * Тесты проверяют корректность работы методов, включая геттеры, сеттеры, equals, hashCode и toString.
 * </p>
 */
class SubscriberTest {

    private static final Long TEST_ID = 1L;
    private static final String TEST_MSISDN = "79991112233";
    private static final String OTHER_MSISDN = "79992223344";

    private Subscriber subscriber;

    @BeforeEach
    void setUp() {
        subscriber = new Subscriber(TEST_ID, TEST_MSISDN);
    }

    @Test
    void testGettersAndSetters() {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(TEST_ID);
        subscriber.setMsisdn(TEST_MSISDN);

        assertEquals(TEST_ID, subscriber.getId());
        assertEquals(TEST_MSISDN, subscriber.getMsisdn());
    }

    @Test
    void testEqualsAndHashCode() {
        Subscriber subscriber1 = new Subscriber(TEST_ID, TEST_MSISDN);
        Subscriber subscriber2 = new Subscriber(TEST_ID, TEST_MSISDN);
        Subscriber differentSubscriber = new Subscriber(2L, OTHER_MSISDN);

        assertEquals(subscriber1, subscriber2);
        assertEquals(subscriber1.hashCode(), subscriber2.hashCode());

        assertNotEquals(subscriber1, differentSubscriber);
        assertNotEquals(subscriber1.hashCode(), differentSubscriber.hashCode());
    }

    @Test
    void testToString() {
        String expected = "Subscriber(id=" + TEST_ID + ", msisdn=" + TEST_MSISDN + ")";
        assertEquals(expected, subscriber.toString());
    }

    @Test
    void testCanEqual() {
        Subscriber anotherSubscriber = new Subscriber(TEST_ID, TEST_MSISDN);
        assertTrue(subscriber.canEqual(anotherSubscriber));
        assertFalse(subscriber.canEqual(new Object()));
    }

    @Test
    void testNoArgsConstructor() {
        Subscriber emptySubscriber = new Subscriber();
        assertNotNull(emptySubscriber);
        assertNull(emptySubscriber.getId());
        assertNull(emptySubscriber.getMsisdn());
    }

    @Test
    void testAllArgsConstructor() {
        Subscriber newSubscriber = new Subscriber(TEST_ID, TEST_MSISDN);
        assertEquals(TEST_ID, newSubscriber.getId());
        assertEquals(TEST_MSISDN, newSubscriber.getMsisdn());
    }

    @Test
    void testConstructorWithMsisdn() {
        Subscriber msisdnOnlySubscriber = new Subscriber(TEST_MSISDN);
        assertNull(msisdnOnlySubscriber.getId());
        assertEquals(TEST_MSISDN, msisdnOnlySubscriber.getMsisdn());
    }
}