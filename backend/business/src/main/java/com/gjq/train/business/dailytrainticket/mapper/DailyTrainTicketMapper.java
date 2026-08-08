package com.gjq.train.business.dailytrainticket.mapper;

import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 余票信息 Mapper 接口
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-07
 */
public interface DailyTrainTicketMapper extends BaseMapper<DailyTrainTicket> {

    int deductInventory(
            @Param("id") Long id,
            @Param("ydz") int ydz,
            @Param("edz") int edz,
            @Param("rw") int rw,
            @Param("yw") int yw
    );
}
