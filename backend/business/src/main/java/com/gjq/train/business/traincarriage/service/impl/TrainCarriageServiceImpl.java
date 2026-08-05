package com.gjq.train.business.traincarriage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.gjq.train.business.traincarriage.enums.SeatTypeEnum;
import com.gjq.train.business.traincarriage.mapper.TrainCarriageMapper;
import com.gjq.train.business.traincarriage.req.TrainCarriageQueryReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageSaveReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageUpdateReq;
import com.gjq.train.business.traincarriage.resp.TrainCarriageQueryResp;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 火车车厢 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Service
public class TrainCarriageServiceImpl
        extends ServiceImpl<TrainCarriageMapper, TrainCarriage>
        implements TrainCarriageService {

    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq request) {
        //1. 校验座位类型和同一车次下的厢号唯一性
        SeatTypeEnum seatType = requireSeatType(request.getSeatType());
        checkIndexUnique(request.getTrainCode(), request.getIndex(), null);

        //2. 根据座位类型和排数自动计算列数、座位数
        TrainCarriage trainCarriage = BeanUtil.copyProperties(
                request,
                TrainCarriage.class
        );
        calculateSeatLayout(trainCarriage, seatType);

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        trainCarriage.setCreateTime(now);
        trainCarriage.setUpdateTime(now);
        trainCarriageMapper.insert(trainCarriage);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除火车车厢
        int affectedRows = trainCarriageMapper.deleteById(id);

        //2. 未删除任何记录时提示车厢不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_NOT_EXIST
            );
        }
    }

    @Override
    public void update(TrainCarriageUpdateReq request) {
        //1. 确认需要修改的火车车厢存在
        if (trainCarriageMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_NOT_EXIST
            );
        }

        //2. 校验座位类型和修改后的厢号唯一性
        SeatTypeEnum seatType = requireSeatType(request.getSeatType());
        checkIndexUnique(
                request.getTrainCode(),
                request.getIndex(),
                request.getId()
        );

        //3. 转换实体并重新计算列数、座位数
        TrainCarriage trainCarriage = BeanUtil.copyProperties(
                request,
                TrainCarriage.class
        );
        calculateSeatLayout(trainCarriage, seatType);
        trainCarriage.setUpdateTime(LocalDateTime.now());
        trainCarriageMapper.updateById(trainCarriage);
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(
            TrainCarriageQueryReq request
    ) {
        //1. 按车次编号和厢号升序构造分页查询
        Page<TrainCarriage> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<TrainCarriage> queryWrapper =
                new LambdaQueryWrapper<TrainCarriage>()
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                TrainCarriage::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByAsc(TrainCarriage::getTrainCode)
                        .orderByAsc(TrainCarriage::getIndex);

        //2. 查询火车车厢分页数据
        Page<TrainCarriage> trainCarriagePage =
                trainCarriageMapper.selectPage(page, queryWrapper);

        //3. 将实体转换为响应对象
        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(
                trainCarriagePage.getRecords(),
                TrainCarriageQueryResp.class
        );

        //4. 组装分页响应
        PageResp<TrainCarriageQueryResp> response = new PageResp<>();
        response.setTotal(trainCarriagePage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public List<TrainCarriage> listByTrainCode(String trainCode) {
        return trainCarriageMapper.selectList(
                new LambdaQueryWrapper<TrainCarriage>()
                        .eq(TrainCarriage::getTrainCode, trainCode)
                        .orderByAsc(TrainCarriage::getIndex)
        );
    }

    private SeatTypeEnum requireSeatType(String code) {
        SeatTypeEnum seatType = SeatTypeEnum.fromCode(code);
        if (seatType == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_SEAT_TYPE_INVALID
            );
        }
        return seatType;
    }

    private void calculateSeatLayout(
            TrainCarriage trainCarriage,
            SeatTypeEnum seatType
    ) {
        int columnCount = seatType.getColumnCount();
        trainCarriage.setColumnCount(columnCount);
        trainCarriage.setSeatCount(columnCount * trainCarriage.getRowCount());
    }

    private void checkIndexUnique(
            String trainCode,
            Integer index,
            Long excludeId
    ) {
        //1. 按车次编号和厢号查询现有记录
        LambdaQueryWrapper<TrainCarriage> queryWrapper =
                new LambdaQueryWrapper<TrainCarriage>()
                        .eq(TrainCarriage::getTrainCode, trainCode)
                        .eq(TrainCarriage::getIndex, index)
                        .ne(excludeId != null, TrainCarriage::getId, excludeId);

        //2. 已有其他记录使用该厢号时拒绝保存
        if (trainCarriageMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_EXIST
            );
        }
    }

}
