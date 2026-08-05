package com.gjq.train.business.traincarriage.enums;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatTypeEnumTests {

    @Test
    void shouldRecognizeSupportedSeatTypesAndColumnCounts() {
        assertTrue(SeatTypeEnum.contains("1"));
        assertTrue(SeatTypeEnum.contains("2"));
        assertTrue(SeatTypeEnum.contains("3"));
        assertTrue(SeatTypeEnum.contains("4"));
        assertFalse(SeatTypeEnum.contains("5"));
        assertEquals(4, SeatTypeEnum.fromCode("1").getColumnCount());
        assertEquals(5, SeatTypeEnum.fromCode("2").getColumnCount());
        assertEquals(
                new BigDecimal("0.4"),
                SeatTypeEnum.FIRST_CLASS.getPrice()
        );
        assertEquals(
                new BigDecimal("0.3"),
                SeatTypeEnum.SECOND_CLASS.getPrice()
        );
        assertEquals(
                new BigDecimal("0.6"),
                SeatTypeEnum.SOFT_SLEEPER.getPrice()
        );
        assertEquals(
                new BigDecimal("0.5"),
                SeatTypeEnum.HARD_SLEEPER.getPrice()
        );
    }
}
