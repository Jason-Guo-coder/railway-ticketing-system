package com.gjq.train.business.dailytrainticket.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.mapper.DailyTrainTicketMapper;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketSaveReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketUpdateReq;
import com.gjq.train.business.dailytrainticket.resp.DailyTrainTicketQueryResp;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.business.train.enums.TrainTypeEnum;
import com.gjq.train.business.traincarriage.enums.SeatTypeEnum;
import com.gjq.train.business.trainstation.entity.TrainStation;
import com.gjq.train.business.trainstation.service.TrainStationService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日余票业务实现。
 */
@Service
public class DailyTrainTicketServiceImpl
        extends ServiceImpl<DailyTrainTicketMapper, DailyTrainTicket>
        implements DailyTrainTicketService {

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    private TrainStationService trainStationService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Override
    public void save(DailyTrainTicketSaveReq request) {
        //1. 校验同日、同车次、同出发到达区间唯一性
        checkRouteUnique(request, null);

        //2. 转换实体并设置新增、修改时间
        DailyTrainTicket ticket = BeanUtil.copyProperties(
                request,
                DailyTrainTicket.class
        );
        LocalDateTime now = LocalDateTime.now();
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);

        //3. 保存每日余票记录
        dailyTrainTicketMapper.insert(ticket);
    }

    @Override
    public void delete(Long id) {
        //1. 根据ID删除每日余票
        int affectedRows = dailyTrainTicketMapper.deleteById(id);

        //2. 未删除任何记录时提示每日余票不存在
        if (affectedRows == 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_TICKET_NOT_EXIST
            );
        }
    }

    @Override
    public void update(DailyTrainTicketUpdateReq request) {
        //1. 确认需要修改的每日余票存在
        if (dailyTrainTicketMapper.selectById(request.getId()) == null) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_TICKET_NOT_EXIST
            );
        }

        //2. 排除当前记录校验出发到达区间唯一性
        checkRouteUnique(request, request.getId());

        //3. 转换实体并更新可编辑字段
        DailyTrainTicket ticket = BeanUtil.copyProperties(
                request,
                DailyTrainTicket.class
        );
        ticket.setUpdateTime(LocalDateTime.now());
        dailyTrainTicketMapper.updateById(ticket);
    }

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(
            DailyTrainTicketQueryReq request
    ) {
        //1. 按日期、车次、出发站和到达站构造分页查询条件
        Page<DailyTrainTicket> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<DailyTrainTicket> queryWrapper =
                new LambdaQueryWrapper<DailyTrainTicket>()
                        .eq(
                                request.getDate() != null,
                                DailyTrainTicket::getDate,
                                request.getDate()
                        )
                        .eq(
                                StringUtils.hasText(request.getTrainCode()),
                                DailyTrainTicket::getTrainCode,
                                request.getTrainCode()
                        )
                        .eq(
                                StringUtils.hasText(request.getStart()),
                                DailyTrainTicket::getStart,
                                request.getStart()
                        )
                        .eq(
                                StringUtils.hasText(request.getEnd()),
                                DailyTrainTicket::getEnd,
                                request.getEnd()
                        )
                        .orderByDesc(DailyTrainTicket::getDate)
                        .orderByAsc(DailyTrainTicket::getTrainCode)
                        .orderByAsc(DailyTrainTicket::getStartIndex)
                        .orderByAsc(DailyTrainTicket::getEndIndex);

        //2. 查询每日余票分页数据
        Page<DailyTrainTicket> ticketPage =
                dailyTrainTicketMapper.selectPage(page, queryWrapper);

        //3. 转换响应对象并组装分页结果
        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(
                ticketPage.getRecords(),
                DailyTrainTicketQueryResp.class
        );
        PageResp<DailyTrainTicketQueryResp> response = new PageResp<>();
        response.setTotal(ticketPage.getTotal());
        response.setList(list);
        return response;
    }

    @Override
    public DailyTrainTicket selectByUnique(
            LocalDate date,
            String trainCode,
            String start,
            String end
    ) {
        //1. 按日期、车次、出发站和到达站构造唯一键条件
        LambdaQueryWrapper<DailyTrainTicket> queryWrapper =
                new LambdaQueryWrapper<DailyTrainTicket>()
                        .eq(DailyTrainTicket::getDate, date)
                        .eq(DailyTrainTicket::getTrainCode, trainCode)
                        .eq(DailyTrainTicket::getStart, start)
                        .eq(DailyTrainTicket::getEnd, end);

        //2. 返回该区间当前的真实余票记录
        return dailyTrainTicketMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public void generateByTrainCode(
            LocalDate date,
            String trainCode,
            String trainType
    ) {
        //1. 清空该日期、该车次已有余票，保证重复生成结果一致
        dailyTrainTicketMapper.delete(
                new LambdaQueryWrapper<DailyTrainTicket>()
                        .eq(DailyTrainTicket::getDate, date)
                        .eq(DailyTrainTicket::getTrainCode, trainCode)
        );

        //2. 按站序查询车次车站；不足两个站时无法形成售票区间
        List<TrainStation> stations = trainStationService.listByTrainCode(trainCode);
        if (stations.size() < 2) {
            return;
        }

        //3. 读取车次票价系数，并一次性统计四种座位数量
        TrainTypeEnum trainTypeEnum = TrainTypeEnum.fromCode(trainType);
        if (trainTypeEnum == null) {
            throw new BusinessException(
                    BusinessExceptionEnum.BUSINESS_TRAIN_TYPE_INVALID
            );
        }
        int ydz = dailyTrainSeatService.countSeat(
                date,
                trainCode,
                SeatTypeEnum.FIRST_CLASS.getCode()
        );
        int edz = dailyTrainSeatService.countSeat(
                date,
                trainCode,
                SeatTypeEnum.SECOND_CLASS.getCode()
        );
        int rw = dailyTrainSeatService.countSeat(
                date,
                trainCode,
                SeatTypeEnum.SOFT_SLEEPER.getCode()
        );
        int yw = dailyTrainSeatService.countSeat(
                date,
                trainCode,
                SeatTypeEnum.HARD_SLEEPER.getCode()
        );
        LocalDateTime now = LocalDateTime.now();

        //4. 依次组合每个出发站和它后面的到达站，生成全部售票区间
        for (int startPosition = 0;
             startPosition < stations.size() - 1;
             startPosition++) {
            TrainStation startStation = stations.get(startPosition);
            BigDecimal distance = BigDecimal.ZERO;

            //① 终点只能选择出发站后面的站，并逐站累加区间里程
            for (int endPosition = startPosition + 1;
                 endPosition < stations.size();
                 endPosition++) {
                TrainStation endStation = stations.get(endPosition);
                distance = distance.add(endStation.getKm());

                //② 组装当前起终点区间的基础信息和余票数量
                DailyTrainTicket ticket = new DailyTrainTicket();
                ticket.setDate(date);
                ticket.setTrainCode(trainCode);
                ticket.setStart(startStation.getName());
                ticket.setStartPinyin(startStation.getNamePinyin());
                ticket.setStartTime(startStation.getOutTime());
                ticket.setStartIndex(startStation.getIndex());
                ticket.setEnd(endStation.getName());
                ticket.setEndPinyin(endStation.getNamePinyin());
                ticket.setEndTime(endStation.getInTime());
                ticket.setEndIndex(endStation.getIndex());
                ticket.setYdz(ydz);
                ticket.setEdz(edz);
                ticket.setRw(rw);
                ticket.setYw(yw);

                //③ 票价 = 区间里程 × 座位每公里单价 × 车次系数
                BigDecimal priceRate = trainTypeEnum.getPriceRate();
                ticket.setYdzPrice(calculatePrice(
                        distance,
                        SeatTypeEnum.FIRST_CLASS,
                        priceRate
                ));
                ticket.setEdzPrice(calculatePrice(
                        distance,
                        SeatTypeEnum.SECOND_CLASS,
                        priceRate
                ));
                ticket.setRwPrice(calculatePrice(
                        distance,
                        SeatTypeEnum.SOFT_SLEEPER,
                        priceRate
                ));
                ticket.setYwPrice(calculatePrice(
                        distance,
                        SeatTypeEnum.HARD_SLEEPER,
                        priceRate
                ));
                ticket.setCreateTime(now);
                ticket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(ticket);
            }
        }
    }

    private BigDecimal calculatePrice(
            BigDecimal distance,
            SeatTypeEnum seatType,
            BigDecimal priceRate
    ) {
        return distance
                .multiply(seatType.getPrice())
                .multiply(priceRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void checkRouteUnique(
            DailyTrainTicketSaveReq request,
            Long excludeId
    ) {
        //1. 按数据库唯一键构造查询条件
        LambdaQueryWrapper<DailyTrainTicket> queryWrapper =
                new LambdaQueryWrapper<DailyTrainTicket>()
                        .eq(DailyTrainTicket::getDate, request.getDate())
                        .eq(
                                DailyTrainTicket::getTrainCode,
                                request.getTrainCode()
                        )
                        .eq(DailyTrainTicket::getStart, request.getStart())
                        .eq(DailyTrainTicket::getEnd, request.getEnd())
                        .ne(
                                excludeId != null,
                                DailyTrainTicket::getId,
                                excludeId
                        );

        //2. 已存在相同区间时阻止保存
        if (dailyTrainTicketMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_DAILY_TRAIN_TICKET_ROUTE_EXIST
            );
        }
    }

}
