package com.gjq.train.business.traincarriage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SeatTypeEnum {

    FIRST_CLASS("1", "一等座", new BigDecimal("0.4"), 4),
    SECOND_CLASS("2", "二等座", new BigDecimal("0.3"), 5),
    SOFT_SLEEPER("3", "软卧", new BigDecimal("0.6"), 4),
    HARD_SLEEPER("4", "硬卧", new BigDecimal("0.5"), 6);

    private final String code;

    private final String description;

    /**
     * 每公里基础票价，单位为元。
     * 票价 = 里程 × SeatTypeEnum.price × TrainTypeEnum.priceRate。
     */
    private final BigDecimal price;

    private final int columnCount;

    public static SeatTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    public static boolean contains(String code) {
        return fromCode(code) != null;
    }
}
