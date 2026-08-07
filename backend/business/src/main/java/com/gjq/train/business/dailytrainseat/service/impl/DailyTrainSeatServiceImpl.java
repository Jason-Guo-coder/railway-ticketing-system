package com.gjq.train.business.dailytrainseat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.mapper.DailyTrainSeatMapper;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatQueryReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatSaveReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatUpdateReq;
import com.gjq.train.business.dailytrainseat.resp.DailyTrainSeatQueryResp;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.business.traincarriage.enums.SeatTypeEnum;
import com.gjq.train.business.trainseat.entity.TrainSeat;
import com.gjq.train.business.trainseat.enums.SeatColEnum;
import com.gjq.train.business.trainseat.service.TrainSeatService;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日座位业务实现。
 */
@Service
public class DailyTrainSeatServiceImpl
        extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat>
        implements DailyTrainSeatService {

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;

    @Override
    public void save(DailyTrainSeatSaveReq request) {
        //1. 校验座位类型和列号是否匹配
        checkSeatTypeAndCol(request.getSeatType(), request.getCol());

        //2. 校验同日同车次同车厢内的位置和座序唯一性
        checkUnique(request, null);

        //3. 转换实体，设置新增和修改时间后保存
        DailyTrainSeat seat = BeanUtil.copyProperties(
                request,
                DailyTrainSeat.class
        );
        LocalDateTime now = LocalDateTime.now();
        seat.setCreateTime(now);
        seat.setUpdateTime(now);
        dailyTrainSeatMapper.insert(seat);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除每日座位
        int affectedRows = dailyTrainSeatMapper.deleteById(id);

        //2. 未删除任何记录时提示数据不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_DAILY_TRAIN_SEAT_NOT_EXIST
            );
        }
    }

    @Override
    public void update(DailyTrainSeatUpdateReq request) {
        //1. 确认需要修改的每日座位存在
        if (dailyTrainSeatMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_DAILY_TRAIN_SEAT_NOT_EXIST
            );
        }

        //2. 校验座位类型、列号和排除当前记录后的唯一性
        checkSeatTypeAndCol(request.getSeatType(), request.getCol());
        checkUnique(request, request.getId());

        //3. 转换实体并更新可编辑字段
        DailyTrainSeat seat = BeanUtil.copyProperties(
                request,
                DailyTrainSeat.class
        );
        seat.setUpdateTime(LocalDateTime.now());
        dailyTrainSeatMapper.updateById(seat);
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(
            DailyTrainSeatQueryReq request
    ) {
        //1. 按日期和车次编号构造分页查询条件
        Page<DailyTrainSeat> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<DailyTrainSeat> queryWrapper =
                new LambdaQueryWrapper<DailyTrainSeat>()
                        .eq(
                                request.getDate() != null,
                                DailyTrainSeat::getDate,
                                request.getDate()
                        )
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                DailyTrainSeat::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByDesc(DailyTrainSeat::getDate)
                        .orderByAsc(DailyTrainSeat::getTrainCode)
                        .orderByAsc(DailyTrainSeat::getCarriageIndex)
                        .orderByAsc(DailyTrainSeat::getCarriageSeatIndex);

        //2. 查询每日座位分页数据
        Page<DailyTrainSeat> seatPage =
                dailyTrainSeatMapper.selectPage(page, queryWrapper);

        //3. 转换响应对象并组装分页结果
        List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(
                seatPage.getRecords(),
                DailyTrainSeatQueryResp.class
        );
        PageResp<DailyTrainSeatQueryResp> response = new PageResp<>();
        response.setTotal(seatPage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public void generateByTrainCode(LocalDate date, String trainCode) {
        //1. 清空该日期、该车次原有的每日座位
        dailyTrainSeatMapper.delete(
                new LambdaQueryWrapper<DailyTrainSeat>()
                        .eq(DailyTrainSeat::getDate, date)
                        .eq(DailyTrainSeat::getTrainCode, trainCode)
        );

        //2. 根据车站数生成每个座位的区间售卖状态
        int stationCount = trainStationService
                .listByTrainCode(trainCode)
                .size();
        String sell = "0".repeat(Math.max(stationCount - 1, 0));

        //3. 查询基础车次下的全部座位
        List<TrainSeat> trainSeats = trainSeatService.listByTrainCode(
                trainCode
        );
        LocalDateTime now = LocalDateTime.now();

        //4. 逐个复制为指定日期的每日座位
        for (TrainSeat trainSeat : trainSeats) {
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(
                    trainSeat,
                    DailyTrainSeat.class
            );
            dailyTrainSeat.setId(null);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
    }

    @Override
    public int countSeat(
            LocalDate date,
            String trainCode,
            String seatType
    ) {
        //1. 按日期、车次和座位类型统计座位
        long count = dailyTrainSeatMapper.selectCount(
                new LambdaQueryWrapper<DailyTrainSeat>()
                        .eq(DailyTrainSeat::getDate, date)
                        .eq(DailyTrainSeat::getTrainCode, trainCode)
                        .eq(DailyTrainSeat::getSeatType, seatType)
        );

        //2. 没有该座位类型时返回-1，供后续余票生成识别
        return count == 0 ? -1 : Math.toIntExact(count);
    }

    private void checkSeatTypeAndCol(String seatType, String col) {
        if (!SeatTypeEnum.contains(seatType)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_TYPE_INVALID
            );
        }
        if (!SeatColEnum.supports(seatType, col)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_COL_INVALID
            );
        }
    }

    private void checkUnique(
            DailyTrainSeatSaveReq request,
            Long excludeId
    ) {
        //1. 校验同日同车次同车厢的排号和列号组合唯一
        LambdaQueryWrapper<DailyTrainSeat> locationWrapper =
                baseUniqueWrapper(request, excludeId)
                        .eq(DailyTrainSeat::getRow, request.getRow())
                        .eq(DailyTrainSeat::getCol, request.getCol());
        if (dailyTrainSeatMapper.selectCount(locationWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_SEAT_LOCATION_EXIST
            );
        }

        //2. 校验同日同车次同车厢的座序唯一
        LambdaQueryWrapper<DailyTrainSeat> indexWrapper =
                baseUniqueWrapper(request, excludeId)
                        .eq(
                                DailyTrainSeat::getCarriageSeatIndex,
                                request.getCarriageSeatIndex()
                        );
        if (dailyTrainSeatMapper.selectCount(indexWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_SEAT_INDEX_EXIST
            );
        }
    }

    private LambdaQueryWrapper<DailyTrainSeat> baseUniqueWrapper(
            DailyTrainSeatSaveReq request,
            Long excludeId
    ) {
        return new LambdaQueryWrapper<DailyTrainSeat>()
                .eq(DailyTrainSeat::getDate, request.getDate())
                .eq(DailyTrainSeat::getTrainCode, request.getTrainCode())
                .eq(
                        DailyTrainSeat::getCarriageIndex,
                        request.getCarriageIndex()
                )
                .ne(
                        excludeId != null,
                        DailyTrainSeat::getId,
                        excludeId
                );
    }
}
