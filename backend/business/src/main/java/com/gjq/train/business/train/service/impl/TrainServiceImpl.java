package com.gjq.train.business.train.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.train.entity.Train;
import com.gjq.train.business.train.enums.TrainTypeEnum;
import com.gjq.train.business.train.mapper.TrainMapper;
import com.gjq.train.business.train.req.TrainQueryReq;
import com.gjq.train.business.train.req.TrainSaveReq;
import com.gjq.train.business.train.req.TrainUpdateReq;
import com.gjq.train.business.train.resp.TrainQueryResp;
import com.gjq.train.business.train.service.TrainService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train>
        implements TrainService {

    @Resource
    private TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq request) {
        //1. 校验车次类型和车次编号唯一性
        checkType(request.getType());
        checkCodeUnique(request.getCode(), null);

        //2. 将请求参数转换为车次实体
        Train train = BeanUtil.copyProperties(request, Train.class);

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        train.setCreateTime(now);
        train.setUpdateTime(now);
        trainMapper.insert(train);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除车次
        int affectedRows = trainMapper.deleteById(id);

        //2. 未删除任何记录时提示车次不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_NOT_EXIST
            );
        }
    }

    @Override
    public void update(TrainUpdateReq request) {
        //1. 确认需要修改的车次存在
        if (trainMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_NOT_EXIST
            );
        }

        //2. 校验车次类型和车次编号唯一性
        checkType(request.getType());
        checkCodeUnique(request.getCode(), request.getId());

        //3. 转换实体并更新可编辑字段
        Train train = BeanUtil.copyProperties(request, Train.class);
        train.setUpdateTime(LocalDateTime.now());
        trainMapper.updateById(train);
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq request) {
        //1. 按车次编号升序构造分页查询
        Page<Train> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Train> queryWrapper =
                new LambdaQueryWrapper<Train>()
                        .orderByAsc(Train::getCode);

        //2. 查询车次分页数据
        Page<Train> trainPage = trainMapper.selectPage(page, queryWrapper);

        //3. 将车次实体转换为响应对象
        List<TrainQueryResp> list = BeanUtil.copyToList(
                trainPage.getRecords(),
                TrainQueryResp.class
        );

        //4. 组装分页响应
        PageResp<TrainQueryResp> response = new PageResp<>();
        response.setTotal(trainPage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public List<TrainQueryResp> queryAll() {
        //1. 按车次编号升序查询全部车次
        List<Train> trains = trainMapper.selectList(
                new LambdaQueryWrapper<Train>()
                        .orderByAsc(Train::getCode)
        );

        //2. 将全部车次转换为响应对象
        return BeanUtil.copyToList(trains, TrainQueryResp.class);
    }

    private void checkType(String type) {
        if (!TrainTypeEnum.contains(type)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_TYPE_INVALID
            );
        }
    }

    private void checkCodeUnique(String code, Long excludeId) {
        //1. 按车次编号查询现有车次
        LambdaQueryWrapper<Train> queryWrapper =
                new LambdaQueryWrapper<Train>()
                        .eq(Train::getCode, code)
                        .ne(excludeId != null, Train::getId, excludeId);

        //2. 已存在相同车次编号时阻止保存
        if (trainMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_CODE_EXIST
            );
        }
    }
}
