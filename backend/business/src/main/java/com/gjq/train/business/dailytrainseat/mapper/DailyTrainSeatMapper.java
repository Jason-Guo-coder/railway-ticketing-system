package com.gjq.train.business.dailytrainseat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 每日座位持久层。
 */
public interface DailyTrainSeatMapper extends BaseMapper<DailyTrainSeat> {

    int updateSellIfMatch(
            @Param("id") Long id,
            @Param("oldSell") String oldSell,
            @Param("newSell") String newSell,
            @Param("updateTime") LocalDateTime updateTime
    );
}
