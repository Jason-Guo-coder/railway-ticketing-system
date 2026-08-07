package com.gjq.train.business.dailytrainstation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.dailytrainstation.entity.DailyTrainStation;
import com.gjq.train.business.dailytrainstation.mapper.DailyTrainStationMapper;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationQueryReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationSaveReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationUpdateReq;
import com.gjq.train.business.dailytrainstation.resp.DailyTrainStationQueryResp;
import com.gjq.train.business.dailytrainstation.service.DailyTrainStationService;
import com.gjq.train.business.trainstation.entity.TrainStation;
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
 * 每日车站业务实现。
 */
@Service
public class DailyTrainStationServiceImpl
        extends ServiceImpl<DailyTrainStationMapper, DailyTrainStation>
        implements DailyTrainStationService {

    @Resource
    private DailyTrainStationMapper dailyTrainStationMapper;

    @Resource
    private TrainStationService trainStationService;

    @Override
    public void save(DailyTrainStationSaveReq request) {
        //1. 校验同日同车次下的站序和站名唯一性
        checkIndexUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getIndex(),
                null
        );
        checkNameUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getName(),
                null
        );

        //2. 将请求参数转换为每日车站实体
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(
                request,
                DailyTrainStation.class
        );

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        dailyTrainStation.setCreateTime(now);
        dailyTrainStation.setUpdateTime(now);
        dailyTrainStationMapper.insert(dailyTrainStation);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除每日车站
        int affectedRows = dailyTrainStationMapper.deleteById(id);

        //2. 未删除任何记录时提示数据不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_STATION_NOT_EXIST
            );
        }
    }

    @Override
    public void update(DailyTrainStationUpdateReq request) {
        //1. 确认需要修改的每日车站存在
        if (dailyTrainStationMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_STATION_NOT_EXIST
            );
        }

        //2. 排除当前记录后校验站序和站名唯一性
        checkIndexUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getIndex(),
                request.getId()
        );
        checkNameUnique(
                request.getDate(),
                request.getTrainCode(),
                request.getName(),
                request.getId()
        );

        //3. 转换实体并更新可编辑字段
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(
                request,
                DailyTrainStation.class
        );
        dailyTrainStation.setUpdateTime(LocalDateTime.now());
        dailyTrainStationMapper.updateById(dailyTrainStation);
    }

    @Override
    public PageResp<DailyTrainStationQueryResp> queryList(
            DailyTrainStationQueryReq request
    ) {
        //1. 按日期和车次编号构造分页查询条件
        Page<DailyTrainStation> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<DailyTrainStation> queryWrapper =
                new LambdaQueryWrapper<DailyTrainStation>()
                        .eq(
                                request.getDate() != null,
                                DailyTrainStation::getDate,
                                request.getDate()
                        )
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                DailyTrainStation::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByDesc(DailyTrainStation::getDate)
                        .orderByAsc(DailyTrainStation::getTrainCode)
                        .orderByAsc(DailyTrainStation::getIndex);

        //2. 查询每日车站分页数据
        Page<DailyTrainStation> dailyTrainStationPage =
                dailyTrainStationMapper.selectPage(page, queryWrapper);

        //3. 将每日车站实体转换为响应对象
        List<DailyTrainStationQueryResp> list = BeanUtil.copyToList(
                dailyTrainStationPage.getRecords(),
                DailyTrainStationQueryResp.class
        );

        //4. 组装分页响应
        PageResp<DailyTrainStationQueryResp> response = new PageResp<>();
        response.setTotal(dailyTrainStationPage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public void generateByTrainCode(LocalDate date, String trainCode) {
        //1. 清空该日期、该车次原有的每日车站
        dailyTrainStationMapper.delete(
                new LambdaQueryWrapper<DailyTrainStation>()
                        .eq(DailyTrainStation::getDate, date)
                        .eq(DailyTrainStation::getTrainCode, trainCode)
        );

        //2. 查询基础车次下的全部车站
        List<TrainStation> trainStations = trainStationService.listByTrainCode(trainCode);
        LocalDateTime now = LocalDateTime.now();

        //3. 逐站复制为指定日期的每日车站
        for (TrainStation trainStation : trainStations) {
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(
                    trainStation,
                    DailyTrainStation.class
            );
            dailyTrainStation.setId(null);
            dailyTrainStation.setDate(date);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
    }

    private void checkIndexUnique(
            LocalDate date,
            String trainCode,
            Integer index,
            Long excludeId
    ) {
        //1. 按日期、车次和站序查询现有记录
        LambdaQueryWrapper<DailyTrainStation> queryWrapper =
                new LambdaQueryWrapper<DailyTrainStation>()
                        .eq(DailyTrainStation::getDate, date)
                        .eq(DailyTrainStation::getTrainCode, trainCode)
                        .eq(DailyTrainStation::getIndex, index)
                        .ne(
                                excludeId != null,
                                DailyTrainStation::getId,
                                excludeId
                        );

        //2. 已存在相同站序时阻止保存
        if (dailyTrainStationMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_STATION_INDEX_EXIST
            );
        }
    }

    private void checkNameUnique(
            LocalDate date,
            String trainCode,
            String name,
            Long excludeId
    ) {
        //1. 按日期、车次和站名查询现有记录
        LambdaQueryWrapper<DailyTrainStation> queryWrapper =
                new LambdaQueryWrapper<DailyTrainStation>()
                        .eq(DailyTrainStation::getDate, date)
                        .eq(DailyTrainStation::getTrainCode, trainCode)
                        .eq(DailyTrainStation::getName, name)
                        .ne(
                                excludeId != null,
                                DailyTrainStation::getId,
                                excludeId
                        );

        //2. 已存在相同站名时阻止保存
        if (dailyTrainStationMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_STATION_NAME_EXIST
            );
        }
    }
}
