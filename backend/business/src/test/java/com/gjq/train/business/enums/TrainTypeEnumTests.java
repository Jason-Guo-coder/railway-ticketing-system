package com.gjq.train.business.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainTypeEnumTests {

    @Test
    void shouldRecognizeSupportedTrainTypes() {
        assertTrue(TrainTypeEnum.contains("G"));
        assertTrue(TrainTypeEnum.contains("D"));
        assertTrue(TrainTypeEnum.contains("K"));
        assertFalse(TrainTypeEnum.contains("Z"));
    }
}
