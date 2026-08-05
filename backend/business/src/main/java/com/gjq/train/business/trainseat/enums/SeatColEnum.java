package com.gjq.train.business.trainseat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SeatColEnum {

    FIRST_A("A", "1"),
    FIRST_C("C", "1"),
    FIRST_D("D", "1"),
    FIRST_F("F", "1"),
    SECOND_A("A", "2"),
    SECOND_B("B", "2"),
    SECOND_C("C", "2"),
    SECOND_D("D", "2"),
    SECOND_F("F", "2"),
    SOFT_A("A", "3"),
    SOFT_B("B", "3"),
    SOFT_C("C", "3"),
    SOFT_D("D", "3"),
    HARD_A("A", "4"),
    HARD_B("B", "4"),
    HARD_C("C", "4"),
    HARD_D("D", "4"),
    HARD_E("E", "4"),
    HARD_F("F", "4");

    private final String code;

    private final String seatType;

    public static boolean supports(String seatType, String code) {
        return Arrays.stream(values()).anyMatch(
                item -> item.seatType.equals(seatType)
                        && item.code.equals(code)
        );
    }

    public static List<String> columnsFor(String seatType) {
        return Arrays.stream(values())
                .filter(item -> item.seatType.equals(seatType))
                .map(SeatColEnum::getCode)
                .toList();
    }
}
