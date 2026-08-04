package com.gjq.train.member.enums;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PassengerTypeEnumTests {

    @Test
    void shouldExposePassengerTypesForFrontendGeneration() {
        List<Map<String, String>> types = PassengerTypeEnum.getEnumList()
                .stream()
                .map(item -> (Map<String, String>) item)
                .toList();

        assertEquals(
                List.of(
                        Map.of("code", "1", "desc", "成人"),
                        Map.of("code", "2", "desc", "儿童"),
                        Map.of("code", "3", "desc", "学生")
                ),
                types
        );
    }
}
