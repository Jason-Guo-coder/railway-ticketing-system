package com.gjq.train.business.train.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TrainTypeEnum {

    G("G", "高铁", new BigDecimal("1.2")),
    D("D", "动车", new BigDecimal("1")),
    K("K", "快速", new BigDecimal("0.8"));

    private final String code;

    private final String description;

    /**
     * 车次票价系数。
     * 票价 = 里程 × SeatTypeEnum.price × TrainTypeEnum.priceRate。
     */
    private final BigDecimal priceRate;

    public static boolean contains(String code) {
        return Arrays.stream(values())
                .anyMatch(item -> item.code.equals(code));
    }
}
