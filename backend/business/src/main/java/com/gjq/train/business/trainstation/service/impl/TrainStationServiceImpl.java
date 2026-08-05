package com.gjq.train.business.trainstation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.mapper.TrainStationMapper;
import com.gjq.train.business.trainstation.req.TrainStationQueryReq;
import com.gjq.train.business.trainstation.req.TrainStationSaveReq;
import com.gjq.train.business.trainstation.req.TrainStationUpdateReq;
import com.gjq.train.business.trainstation.resp.TrainStationQueryResp;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 火车车站 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Service
public class TrainStationServiceImpl
        extends ServiceImpl<TrainStationMapper, TrainStation>
        implements TrainStationService {

    @Resource
    private TrainStationMapper trainStationMapper;

    @Override
    public void save(TrainStationSaveReq request) {
        //1. 校验同一车次下的站序和站名唯一性
        checkIndexUnique(request.getTrainCode(), request.getIndex(), null);
        checkNameUnique(request.getTrainCode(), request.getName(), null);

        //2. 将请求参数转换为车次车站实体
        TrainStation trainStation = BeanUtil.copyProperties(
                request,
                TrainStation.class
        );

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        trainStation.setCreateTime(now);
        trainStation.setUpdateTime(now);
        trainStationMapper.insert(trainStation);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除车次车站
        int affectedRows = trainStationMapper.deleteById(id);

        //2. 未删除任何记录时提示车次车站不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NOT_EXIST
            );
        }
    }

    @Override
    public void update(TrainStationUpdateReq request) {
        //1. 确认需要修改的车次车站存在
        if (trainStationMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NOT_EXIST
            );
        }

        //2. 校验同一车次下的新站序和新站名未被其他记录使用
        checkIndexUnique(
                request.getTrainCode(),
                request.getIndex(),
                request.getId()
        );
        checkNameUnique(
                request.getTrainCode(),
                request.getName(),
                request.getId()
        );

        //3. 转换实体并更新可编辑字段
        TrainStation trainStation = BeanUtil.copyProperties(
                request,
                TrainStation.class
        );
        trainStation.setUpdateTime(LocalDateTime.now());
        trainStationMapper.updateById(trainStation);
    }

    @Override
    public PageResp<TrainStationQueryResp> queryList(
            TrainStationQueryReq request
    ) {
        //1. 按车次编号和站序升序构造分页查询
        Page<TrainStation> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<TrainStation> queryWrapper =
                new LambdaQueryWrapper<TrainStation>()
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                TrainStation::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByAsc(TrainStation::getTrainCode)
                        .orderByAsc(TrainStation::getIndex);

        //2. 查询车次车站分页数据
        Page<TrainStation> trainStationPage = trainStationMapper.selectPage(
                page,
                queryWrapper
        );

        //3. 将实体转换为响应对象
        List<TrainStationQueryResp> list = BeanUtil.copyToList(
                trainStationPage.getRecords(),
                TrainStationQueryResp.class
        );

        //4. 组装分页响应
        PageResp<TrainStationQueryResp> response = new PageResp<>();
        response.setTotal(trainStationPage.getTotal());
        response.setList(list);
        return response;
    }

    private void checkIndexUnique(
            String trainCode,
            Integer index,
            Long excludeId
    ) {
        //1. 按车次编号和站序查询现有记录
        LambdaQueryWrapper<TrainStation> queryWrapper =
                new LambdaQueryWrapper<TrainStation>()
                        .eq(TrainStation::getTrainCode, trainCode)
                        .eq(TrainStation::getIndex, index)
                        .ne(
                                excludeId != null,
                                TrainStation::getId,
                                excludeId
                        );

        //2. 已存在相同站序时阻止保存
        if (trainStationMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_EXIST
            );
        }
    }

    private void checkNameUnique(
            String trainCode,
            String name,
            Long excludeId
    ) {
        //1. 按车次编号和站名查询现有记录
        LambdaQueryWrapper<TrainStation> queryWrapper =
                new LambdaQueryWrapper<TrainStation>()
                        .eq(TrainStation::getTrainCode, trainCode)
                        .eq(TrainStation::getName, name)
                        .ne(
                                excludeId != null,
                                TrainStation::getId,
                                excludeId
                        );

        //2. 已存在相同站名时阻止保存
        if (trainStationMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_EXIST
            );
        }
    }
}
