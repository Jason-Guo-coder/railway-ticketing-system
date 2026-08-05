package com.gjq.train.business.trainseat.enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatColEnumTests {

    @Test
    void shouldReturnColumnsForSeatType() {
        assertEquals(
                List.of("A", "C", "D", "F"),
                SeatColEnum.columnsFor("1")
        );
        assertEquals(
                List.of("A", "B", "C", "D", "F"),
                SeatColEnum.columnsFor("2")
        );
        assertTrue(SeatColEnum.supports("2", "B"));
        assertFalse(SeatColEnum.supports("1", "B"));
    }
}
