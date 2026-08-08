package com.gjq.train.business.confirmorder.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConfirmOrderStatusEnumTests {

    @Test
    void shouldResolveAllOrderStatusesByCode() {
        assertEquals(
                ConfirmOrderStatusEnum.INIT,
                ConfirmOrderStatusEnum.fromCode("I")
        );
        assertEquals(
                ConfirmOrderStatusEnum.PENDING,
                ConfirmOrderStatusEnum.fromCode("P")
        );
        assertEquals(
                ConfirmOrderStatusEnum.SUCCESS,
                ConfirmOrderStatusEnum.fromCode("S")
        );
        assertEquals(
                ConfirmOrderStatusEnum.FAILURE,
                ConfirmOrderStatusEnum.fromCode("F")
        );
        assertEquals(
                ConfirmOrderStatusEnum.EMPTY,
                ConfirmOrderStatusEnum.fromCode("E")
        );
        assertEquals(
                ConfirmOrderStatusEnum.CANCEL,
                ConfirmOrderStatusEnum.fromCode("C")
        );
        assertNull(ConfirmOrderStatusEnum.fromCode("X"));
    }
}
