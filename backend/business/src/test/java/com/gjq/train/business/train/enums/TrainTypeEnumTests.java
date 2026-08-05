package com.gjq.train.business.train.enums;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainTypeEnumTests {

    @Test
    void shouldRecognizeSupportedTrainTypes() {
        assertTrue(TrainTypeEnum.contains("G"));
        assertTrue(TrainTypeEnum.contains("D"));
        assertTrue(TrainTypeEnum.contains("K"));
        assertFalse(TrainTypeEnum.contains("Z"));
        assertEquals(new BigDecimal("1.2"), TrainTypeEnum.G.getPriceRate());
        assertEquals(new BigDecimal("1"), TrainTypeEnum.D.getPriceRate());
        assertEquals(new BigDecimal("0.8"), TrainTypeEnum.K.getPriceRate());
    }
}
