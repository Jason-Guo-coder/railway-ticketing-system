package com.gjq.train.business.dailytrain.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.dailytrain.entity.DailyTrain;
import com.gjq.train.business.dailytrain.mapper.DailyTrainMapper;
import com.gjq.train.business.dailytrain.req.DailyTrainQueryReq;
import com.gjq.train.business.dailytrain.req.DailyTrainSaveReq;
import com.gjq.train.business.dailytrain.req.DailyTrainUpdateReq;
import com.gjq.train.business.dailytrain.resp.DailyTrainQueryResp;
import com.gjq.train.business.dailytrain.service.DailyTrainService;
import com.gjq.train.business.train.enums.TrainTypeEnum;
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
 * <p>
 * 每日车次 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-06
 */
@Service
public class DailyTrainServiceImpl
        extends ServiceImpl<DailyTrainMapper, DailyTrain>
        implements DailyTrainService {

    @Resource
    private DailyTrainMapper dailyTrainMapper;

    @Override
    public void save(DailyTrainSaveReq request) {
        //1. 校验车次类型和日期车次编号唯一性
        checkType(request.getType());
        checkDateCodeUnique(request.getDate(), request.getCode(), null);

        //2. 将请求参数转换为每日车次实体
        DailyTrain dailyTrain = BeanUtil.copyProperties(
                request,
                DailyTrain.class
        );

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrainMapper.insert(dailyTrain);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除每日车次
        int affectedRows = dailyTrainMapper.deleteById(id);

        //2. 未删除任何记录时提示每日车次不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_DAILY_TRAIN_NOT_EXIST
            );
        }
    }

    @Override
    public void update(DailyTrainUpdateReq request) {
        //1. 确认需要修改的每日车次存在
        if (dailyTrainMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_DAILY_TRAIN_NOT_EXIST
            );
        }

        //2. 校验车次类型和日期车次编号唯一性
        checkType(request.getType());
        checkDateCodeUnique(
                request.getDate(),
                request.getCode(),
                request.getId()
        );

        //3. 转换实体并更新可编辑字段
        DailyTrain dailyTrain = BeanUtil.copyProperties(
                request,
                DailyTrain.class
        );
        dailyTrain.setUpdateTime(LocalDateTime.now());
        dailyTrainMapper.updateById(dailyTrain);
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(
            DailyTrainQueryReq request
    ) {
        //1. 按日期和车次编号构造分页查询条件
        Page<DailyTrain> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<DailyTrain> queryWrapper =
                new LambdaQueryWrapper<DailyTrain>()
                        .eq(
                                request.getDate() != null,
                                DailyTrain::getDate,
                                request.getDate()
                        )
                        .eq(
                                StringUtils.hasText(request.getCode()),
                                DailyTrain::getCode,
                                request.getCode()
                        )
                        .orderByDesc(DailyTrain::getDate)
                        .orderByAsc(DailyTrain::getCode);

        //2. 查询每日车次分页数据
        Page<DailyTrain> dailyTrainPage = dailyTrainMapper.selectPage(
                page,
                queryWrapper
        );

        //3. 将每日车次实体转换为响应对象
        List<DailyTrainQueryResp> list = BeanUtil.copyToList(
                dailyTrainPage.getRecords(),
                DailyTrainQueryResp.class
        );

        //4. 组装分页响应
        PageResp<DailyTrainQueryResp> response = new PageResp<>();
        response.setTotal(dailyTrainPage.getTotal());
        response.setList(list);
        return response;
    }

    private void checkType(String type) {
        if (!TrainTypeEnum.contains(type)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_TYPE_INVALID
            );
        }
    }

    private void checkDateCodeUnique(
            LocalDate date,
            String code,
            Long excludeId
    ) {
        //1. 按日期和车次编号查询现有每日车次
        LambdaQueryWrapper<DailyTrain> queryWrapper =
                new LambdaQueryWrapper<DailyTrain>()
                        .eq(DailyTrain::getDate, date)
                        .eq(DailyTrain::getCode, code)
                        .ne(
                                excludeId != null,
                                DailyTrain::getId,
                                excludeId
                        );

        //2. 已存在相同日期和车次编号时阻止保存
        if (dailyTrainMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_DATE_CODE_EXIST
            );
        }
    }

}
