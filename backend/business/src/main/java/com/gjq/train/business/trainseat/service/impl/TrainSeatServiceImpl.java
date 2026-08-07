package com.gjq.train.business.trainseat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.enums.SeatTypeEnum;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
import com.gjq.train.business.trainseat.entity.TrainSeat;
import com.gjq.train.business.trainseat.enums.SeatColEnum;
import com.gjq.train.business.trainseat.mapper.TrainSeatMapper;
import com.gjq.train.business.trainseat.req.TrainSeatQueryReq;
import com.gjq.train.business.trainseat.req.TrainSeatSaveReq;
import com.gjq.train.business.trainseat.req.TrainSeatUpdateReq;
import com.gjq.train.business.trainseat.resp.TrainSeatQueryResp;
import com.gjq.train.business.trainseat.service.TrainSeatService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 座位 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Service
public class TrainSeatServiceImpl
        extends ServiceImpl<TrainSeatMapper, TrainSeat>
        implements TrainSeatService {

    @Resource
    private TrainSeatMapper trainSeatMapper;

    @Resource
    private TrainCarriageService trainCarriageService;

    @Override
    public void save(TrainSeatSaveReq request) {
        //1. 校验座位类型、列号和同车厢唯一性
        checkSeatTypeAndCol(request.getSeatType(), request.getCol());
        checkUnique(request, null);

        //2. 将请求参数转换为座位实体
        TrainSeat trainSeat = BeanUtil.copyProperties(
                request,
                TrainSeat.class
        );

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        trainSeat.setCreateTime(now);
        trainSeat.setUpdateTime(now);
        trainSeatMapper.insert(trainSeat);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除座位
        int affectedRows = trainSeatMapper.deleteById(id);

        //2. 未删除任何记录时提示座位不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_NOT_EXIST
            );
        }
    }

    @Override
    public void update(TrainSeatUpdateReq request) {
        //1. 确认需要修改的座位存在
        if (trainSeatMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_NOT_EXIST
            );
        }

        //2. 校验座位类型、列号和修改后的唯一性
        checkSeatTypeAndCol(request.getSeatType(), request.getCol());
        checkUnique(request, request.getId());

        //3. 转换实体并更新可编辑字段
        TrainSeat trainSeat = BeanUtil.copyProperties(
                request,
                TrainSeat.class
        );
        trainSeat.setUpdateTime(LocalDateTime.now());
        trainSeatMapper.updateById(trainSeat);
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(
            TrainSeatQueryReq request
    ) {
        //1. 按车次、厢序和同车厢座序构造分页查询
        Page<TrainSeat> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<TrainSeat> queryWrapper =
                new LambdaQueryWrapper<TrainSeat>()
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                TrainSeat::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByAsc(TrainSeat::getTrainCode)
                        .orderByAsc(TrainSeat::getCarriageIndex)
                        .orderByAsc(TrainSeat::getCarriageSeatIndex);

        //2. 查询座位分页数据
        Page<TrainSeat> trainSeatPage = trainSeatMapper.selectPage(
                page,
                queryWrapper
        );

        //3. 将座位实体转换为响应对象
        List<TrainSeatQueryResp> list = BeanUtil.copyToList(
                trainSeatPage.getRecords(),
                TrainSeatQueryResp.class
        );

        //4. 组装分页响应
        PageResp<TrainSeatQueryResp> response = new PageResp<>();
        response.setTotal(trainSeatPage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    @Transactional
    public void generateByTrainCode(String trainCode) {
        //① 查询车厢，没有车厢时不能生成座位
        List<TrainCarriage> carriages =
                trainCarriageService.listByTrainCode(trainCode);
        if (carriages.isEmpty()) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_EMPTY
            );
        }

        //② 清空当前车次原有座位，避免重复生成
        trainSeatMapper.delete(
                new LambdaQueryWrapper<TrainSeat>()
                        .eq(TrainSeat::getTrainCode, trainCode)
        );

        //③ 统一生成时间，保证本次生成的座位时间一致
        LocalDateTime now = LocalDateTime.now();

        //④ 根据每节车厢的排数和座位类型生成座位
        for (TrainCarriage carriage : carriages) {
            generateCarriageSeats(trainCode, carriage, now);
        }
    }

    @Override
    public List<TrainSeat> listByTrainCode(String trainCode) {
        return trainSeatMapper.selectList(
                new LambdaQueryWrapper<TrainSeat>()
                        .eq(TrainSeat::getTrainCode, trainCode)
                        .orderByAsc(TrainSeat::getCarriageIndex)
                        .orderByAsc(TrainSeat::getCarriageSeatIndex)
        );
    }

    private void generateCarriageSeats(
            String trainCode,
            TrainCarriage carriage,
            LocalDateTime now
    ) {
        //① 根据座位类型获取列号，例如二等座为A、B、C、D、F
        List<String> columns = SeatColEnum.columnsFor(carriage.getSeatType());
        int seatIndex = 1;

        //② 从第一排开始，逐排生成当前车厢的座位
        for (int row = 1; row <= carriage.getRowCount(); row++) {
            String rowNumber = String.format("%02d", row);

            //③ 遍历当前排的所有列，每个排号和列号组成一个座位
            for (String column : columns) {
                TrainSeat seat = new TrainSeat();
                seat.setTrainCode(trainCode);
                seat.setCarriageIndex(carriage.getIndex());
                seat.setRow(rowNumber);
                seat.setCol(column);
                seat.setSeatType(carriage.getSeatType());
                seat.setCarriageSeatIndex(seatIndex++);
                seat.setCreateTime(now);
                seat.setUpdateTime(now);
                trainSeatMapper.insert(seat);
            }
        }
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

    private void checkUnique(TrainSeatSaveReq request, Long excludeId) {
        //1. 校验同一车厢内的排号和列号组合唯一
        LambdaQueryWrapper<TrainSeat> locationWrapper =
                new LambdaQueryWrapper<TrainSeat>()
                        .eq(TrainSeat::getTrainCode, request.getTrainCode())
                        .eq(
                                TrainSeat::getCarriageIndex,
                                request.getCarriageIndex()
                        )
                        .eq(TrainSeat::getRow, request.getRow())
                        .eq(TrainSeat::getCol, request.getCol())
                        .ne(excludeId != null, TrainSeat::getId, excludeId);
        if (trainSeatMapper.selectCount(locationWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_LOCATION_EXIST
            );
        }

        //2. 校验同一车厢内的座序唯一
        LambdaQueryWrapper<TrainSeat> indexWrapper =
                new LambdaQueryWrapper<TrainSeat>()
                        .eq(TrainSeat::getTrainCode, request.getTrainCode())
                        .eq(
                                TrainSeat::getCarriageIndex,
                                request.getCarriageIndex()
                        )
                        .eq(
                                TrainSeat::getCarriageSeatIndex,
                                request.getCarriageSeatIndex()
                        )
                        .ne(excludeId != null, TrainSeat::getId, excludeId);
        if (trainSeatMapper.selectCount(indexWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_SEAT_INDEX_EXIST
            );
        }
    }

}
