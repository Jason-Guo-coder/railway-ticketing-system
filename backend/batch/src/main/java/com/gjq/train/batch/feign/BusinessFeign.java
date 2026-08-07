package com.gjq.train.batch.feign;

import com.gjq.train.common.resp.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

/**
 * Batch调用Business每日数据接口的声明式HTTP客户端。
 */
@FeignClient(
        name = "business",
        url = "${business.base-url}",
        configuration = BusinessFeignConfiguration.class
)
public interface BusinessFeign {

    /**
     * 目标日期尚未生成数据时，生成每日车次、车站、车厢和座位。
     */
    @PostMapping("/admin/daily-train/gen-daily-if-absent/{date}")
    Result<Void> generateDaily(@PathVariable("date") LocalDate date);
}
