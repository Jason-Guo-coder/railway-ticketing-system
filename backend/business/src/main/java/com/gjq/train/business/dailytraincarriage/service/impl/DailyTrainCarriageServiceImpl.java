package com.gjq.train.business.dailytraincarriage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.dailytraincarriage.entity.DailyTrainCarriage;
import com.gjq.train.business.dailytraincarriage.mapper.DailyTrainCarriageMapper;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageQueryReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageSaveReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageUpdateReq;
import com.gjq.train.business.dailytraincarriage.resp.DailyTrainCarriageQueryResp;
import com.gjq.train.business.dailytraincarriage.service.DailyTrainCarriageService;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.enums.SeatTypeEnum;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
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
 * 每日车厢业务实现。
 */
@Service
public class DailyTrainCarriageServiceImpl
        extends ServiceImpl<DailyTrainCarriageMapper, DailyTrainCarriage>
        implements DailyTrainCarriageService {

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Resource
    private TrainCarriageService trainCarriageService;

    @Override
    public void save(DailyTrainCarriageSaveReq request) {
        //1. 校验座位类型和同日同车次下的厢序唯一性
        SeatTypeEnum seatType = requireSeatType(request.getSeatType());
        checkIndexUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getIndex(),
                null
        );

        //2. 转换实体并根据座位类型、排数计算列数和座位数
        DailyTrainCarriage carriage = BeanUtil.copyProperties(
                request,
                DailyTrainCarriage.class
        );
        calculateSeatLayout(carriage, seatType);

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        carriage.setCreateTime(now);
        carriage.setUpdateTime(now);
        dailyTrainCarriageMapper.insert(carriage);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除每日车厢
        int affectedRows = dailyTrainCarriageMapper.deleteById(id);

        //2. 未删除任何记录时提示数据不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_CARRIAGE_NOT_EXIST
            );
        }
    }

    @Override
    public void update(DailyTrainCarriageUpdateReq request) {
        //1. 确认需要修改的每日车厢存在
        if (dailyTrainCarriageMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_CARRIAGE_NOT_EXIST
            );
        }

        //2. 校验座位类型，并排除当前记录校验厢序唯一性
        SeatTypeEnum seatType = requireSeatType(request.getSeatType());
        checkIndexUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getIndex(),
                request.getId()
        );

        //3. 转换实体，重新计算座位布局后更新
        DailyTrainCarriage carriage = BeanUtil.copyProperties(
                request,
                DailyTrainCarriage.class
        );
        calculateSeatLayout(carriage, seatType);
        carriage.setUpdateTime(LocalDateTime.now());
        dailyTrainCarriageMapper.updateById(carriage);
    }

    @Override
    public PageResp<DailyTrainCarriageQueryResp> queryList(
            DailyTrainCarriageQueryReq request
    ) {
        //1. 按日期和车次编号构造分页查询条件
        Page<DailyTrainCarriage> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<DailyTrainCarriage> queryWrapper =
                new LambdaQueryWrapper<DailyTrainCarriage>()
                        .eq(
                                request.getDate() != null,
                                DailyTrainCarriage::getDate,
                                request.getDate()
                        )
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                DailyTrainCarriage::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByDesc(DailyTrainCarriage::getDate)
                        .orderByAsc(DailyTrainCarriage::getTrainCode)
                        .orderByAsc(DailyTrainCarriage::getIndex);

        //2. 查询每日车厢分页数据
        Page<DailyTrainCarriage> carriagePage =
                dailyTrainCarriageMapper.selectPage(page, queryWrapper);

        //3. 转换响应对象并组装分页结果
        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(
                carriagePage.getRecords(),
                DailyTrainCarriageQueryResp.class
        );
        PageResp<DailyTrainCarriageQueryResp> response = new PageResp<>();
        response.setTotal(carriagePage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public void generateByTrainCode(LocalDate date, String trainCode) {
        //1. 清空该日期、该车次原有的每日车厢
        dailyTrainCarriageMapper.delete(
                new LambdaQueryWrapper<DailyTrainCarriage>()
                        .eq(DailyTrainCarriage::getDate, date)
                        .eq(DailyTrainCarriage::getTrainCode, trainCode)
        );

        //2. 查询基础车次下的全部车厢
        List<TrainCarriage> trainCarriages = trainCarriageService.listByTrainCode(trainCode);
        LocalDateTime now = LocalDateTime.now();

        //3. 逐节复制为指定日期的每日车厢
        for (TrainCarriage trainCarriage : trainCarriages) {
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(
                    trainCarriage,
                    DailyTrainCarriage.class
            );
            dailyTrainCarriage.setId(null);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriage.setColCount(trainCarriage.getColumnCount());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
    }

    private SeatTypeEnum requireSeatType(String code) {
        SeatTypeEnum seatType = SeatTypeEnum.fromCode(code);
        if (seatType == null) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_TRAIN_CARRIAGE_SEAT_TYPE_INVALID
            );
        }
        return seatType;
    }

    private void calculateSeatLayout(
            DailyTrainCarriage carriage,
            SeatTypeEnum seatType
    ) {
        int colCount = seatType.getColumnCount();
        carriage.setColCount(colCount);
        carriage.setSeatCount(colCount * carriage.getRowCount());
    }

    private void checkIndexUnique(
            LocalDate date,
            String trainCode,
            Integer index,
            Long excludeId
    ) {
        //1. 按日期、车次和厢序查询现有记录
        LambdaQueryWrapper<DailyTrainCarriage> queryWrapper =
                new LambdaQueryWrapper<DailyTrainCarriage>()
                        .eq(DailyTrainCarriage::getDate, date)
                        .eq(DailyTrainCarriage::getTrainCode, trainCode)
                        .eq(DailyTrainCarriage::getIndex, index)
                        .ne(
                                excludeId != null,
                                DailyTrainCarriage::getId,
                                excludeId
                        );

        //2. 已存在相同厢序时阻止保存
        if (dailyTrainCarriageMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_CARRIAGE_INDEX_EXIST
            );
        }
    }
}
