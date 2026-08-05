package com.gjq.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.entity.Station;
import com.gjq.train.business.mapper.StationMapper;
import com.gjq.train.business.req.StationQueryReq;
import com.gjq.train.business.req.StationSaveReq;
import com.gjq.train.business.req.StationUpdateReq;
import com.gjq.train.business.resp.StationQueryResp;
import com.gjq.train.business.service.StationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车站 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Resource
    private StationMapper stationMapper;

    @Override
    public void save(StationSaveReq request) {
        //1. 校验站名唯一性
        checkNameUnique(request.getName(), null);

        //2. 将请求参数转换为车站实体
        Station station = BeanUtil.copyProperties(request, Station.class);

        //3. 设置新增和修改时间后保存
        LocalDateTime now = LocalDateTime.now();
        station.setCreateTime(now);
        station.setUpdateTime(now);
        stationMapper.insert(station);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除车站
        int affectedRows = stationMapper.deleteById(id);

        //2. 未删除任何记录时提示车站不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_STATION_NOT_EXIST
            );
        }
    }

    @Override
    public void update(StationUpdateReq request) {
        //1. 确认需要修改的车站存在
        if (stationMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_STATION_NOT_EXIST
            );
        }

        //2. 校验新站名没有被其他车站使用
        checkNameUnique(request.getName(), request.getId());

        //3. 转换实体并更新可编辑字段
        Station station = BeanUtil.copyProperties(request, Station.class);
        station.setUpdateTime(LocalDateTime.now());
        stationMapper.updateById(station);
    }

    @Override
    public PageResp<StationQueryResp> queryList(
            StationQueryReq request
    ) {
        //1. 按ID倒序构造分页查询
        Page<Station> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Station> queryWrapper =
                new LambdaQueryWrapper<Station>()
                        .orderByDesc(Station::getId);

        //2. 查询车站分页数据
        Page<Station> stationPage = stationMapper.selectPage(
                page,
                queryWrapper
        );

        //3. 将车站实体转换为响应对象
        List<StationQueryResp> list = BeanUtil.copyToList(
                stationPage.getRecords(),
                StationQueryResp.class
        );

        //4. 组装分页响应
        PageResp<StationQueryResp> response = new PageResp<>();
        response.setTotal(stationPage.getTotal());
        response.setList(list);
        return response;
    }

    private void checkNameUnique(String name, Long excludeId) {
        //1. 按站名查询现有车站
        LambdaQueryWrapper<Station> queryWrapper =
                new LambdaQueryWrapper<Station>()
                        .eq(Station::getName, name)
                        .ne(excludeId != null, Station::getId, excludeId);

        //2. 已存在同名车站时阻止保存
        if (stationMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_STATION_NAME_EXIST
            );
        }
    }
}
