package com.gjq.train.business.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TrainTypeEnum {

    G("G", "高铁"),
    D("D", "动车"),
    K("K", "快速");

    private final String code;

    private final String description;

    public static boolean contains(String code) {
        return Arrays.stream(values())
                .anyMatch(item -> item.code.equals(code));
    }
}
