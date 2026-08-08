package com.gjq.train.business.confirmorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.confirmorder.entity.ConfirmOrder;
import com.gjq.train.business.confirmorder.enums.ConfirmOrderStatusEnum;
import com.gjq.train.business.confirmorder.mapper.ConfirmOrderMapper;
import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderTicketReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.business.confirmorder.service.ConfirmOrderTransactionService;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.business.trainseat.enums.SeatColEnum;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 确认订单业务实现。
 */
@Service
public class ConfirmOrderServiceImpl
        extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder>
        implements ConfirmOrderService {

    private static final Logger LOG =
            LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private ConfirmOrderTransactionService confirmOrderTransactionService;

    @Override
    public void doConfirm(ConfirmOrderDoReq request) {
        //1. 使用当前登录会员保存初始状态的确认订单
        LocalDateTime now = LocalDateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(request.getDate());
        confirmOrder.setTrainCode(request.getTrainCode());
        confirmOrder.setStart(request.getStart());
        confirmOrder.setEnd(request.getEnd());
        confirmOrder.setDailyTrainTicketId(
                request.getDailyTrainTicketId()
        );
        confirmOrder.setTickets(JSONUtil.toJsonStr(request.getTickets()));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrderMapper.insert(confirmOrder);

        //2. 按业务唯一键查询数据库中的真实余票
        DailyTrainTicket dailyTrainTicket =
                dailyTrainTicketService.selectByUnique(
                        request.getDate(),
                        request.getTrainCode(),
                        request.getStart(),
                        request.getEnd()
                );
        try {
            if (dailyTrainTicket == null) {
                throw new BusinessException(
                        BusinessExceptionEnum
                                .BUSINESS_DAILY_TRAIN_TICKET_NOT_EXIST
                );
            }

            //3. 使用临时库存预扣，避免确认弹窗取消时影响真实余票
            checkTicketCount(dailyTrainTicket, request.getTickets());

            //4. 查询座位并在内存中完成自动分配或按偏移选座
            List<DailyTrainSeat> dailyTrainSeats =
                    listDailyTrainSeats(request);
            List<DailyTrainSeat> selectedSeats = chooseSeats(
                    request,
                    dailyTrainTicket,
                    dailyTrainSeats
            );

            //5. 在独立事务中更新座位、余票、会员车票和订单状态
            confirmOrderTransactionService.finish(
                    confirmOrder,
                    request,
                    dailyTrainTicket,
                    selectedSeats
            );
            LOG.info(
                    "确认订单{}选座完成：{}",
                    confirmOrder.getId(),
                    request.getTickets()
            );
        } catch (RuntimeException exception) {
            //初始订单必须保留，失败只更新订单状态
            boolean noTicket = exception instanceof BusinessException
                    && (((BusinessException) exception).getExceptionEnum()
                    == BusinessExceptionEnum
                    .BUSINESS_CONFIRM_ORDER_TICKET_NOT_ENOUGH
                    || ((BusinessException) exception).getExceptionEnum()
                    == BusinessExceptionEnum
                    .BUSINESS_CONFIRM_ORDER_SEAT_NOT_ENOUGH);
            confirmOrder.setStatus(
                    noTicket
                            ? ConfirmOrderStatusEnum.EMPTY.getCode()
                            : ConfirmOrderStatusEnum.FAILURE.getCode()
            );
            confirmOrder.setUpdateTime(LocalDateTime.now());
            confirmOrderMapper.updateById(confirmOrder);
            throw exception;
        }
    }

    private void checkTicketCount(
            DailyTrainTicket dailyTrainTicket,
            List<ConfirmOrderTicketReq> tickets
    ) {
        Map<String, Integer> temporaryInventory = new HashMap<>();
        temporaryInventory.put("1", dailyTrainTicket.getYdz());
        temporaryInventory.put("2", dailyTrainTicket.getEdz());
        temporaryInventory.put("3", dailyTrainTicket.getRw());
        temporaryInventory.put("4", dailyTrainTicket.getYw());

        for (ConfirmOrderTicketReq ticket : tickets) {
            Integer count = temporaryInventory.get(ticket.getSeatTypeCode());
            if (count == null) {
                throw new BusinessException(
                        BusinessExceptionEnum
                                .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
                );
            }
            count--;
            temporaryInventory.put(ticket.getSeatTypeCode(), count);
            if (count < 0) {
                throw new BusinessException(
                        BusinessExceptionEnum
                                .BUSINESS_CONFIRM_ORDER_TICKET_NOT_ENOUGH
                );
            }
        }
    }

    private List<DailyTrainSeat> listDailyTrainSeats(
            ConfirmOrderDoReq request
    ) {
        return dailyTrainSeatService.list(
                new LambdaQueryWrapper<DailyTrainSeat>()
                        .eq(DailyTrainSeat::getDate, request.getDate())
                        .eq(
                                DailyTrainSeat::getTrainCode,
                                request.getTrainCode()
                        )
                        .orderByAsc(DailyTrainSeat::getCarriageIndex)
                        .orderByAsc(DailyTrainSeat::getCarriageSeatIndex)
        );
    }

    private List<DailyTrainSeat> chooseSeats(
            ConfirmOrderDoReq request,
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> dailyTrainSeats
    ) {
        List<ConfirmOrderTicketReq> tickets = request.getTickets();
        boolean hasSeat = hasText(tickets.get(0).getSeat());
        boolean mixedSeatSelection = tickets.stream().anyMatch(ticket ->
                hasText(ticket.getSeat()) != hasSeat
        );
        if (mixedSeatSelection) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }

        List<DailyTrainSeat> selectedSeats = hasSeat
                ? chooseSeatsByOffset(
                tickets,
                dailyTrainTicket,
                dailyTrainSeats
        )
                : chooseSeatsAutomatically(
                tickets,
                dailyTrainTicket,
                dailyTrainSeats
        );
        if (selectedSeats.size() != tickets.size()) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_NOT_ENOUGH
            );
        }
        for (int i = 0; i < tickets.size(); i++) {
            tickets.get(i).setSeat(formatSeat(selectedSeats.get(i)));
        }
        return selectedSeats;
    }

    private List<DailyTrainSeat> chooseSeatsAutomatically(
            List<ConfirmOrderTicketReq> tickets,
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> dailyTrainSeats
    ) {
        List<DailyTrainSeat> selectedSeats = new ArrayList<>();
        for (ConfirmOrderTicketReq ticket : tickets) {
            DailyTrainSeat selected = dailyTrainSeats.stream()
                    .filter(seat -> ticket.getSeatTypeCode().equals(
                            seat.getSeatType()
                    ))
                    .filter(seat -> isAvailable(
                            seat,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    ))
                    .findFirst()
                    .orElse(null);
            if (selected == null) {
                throw new BusinessException(
                        BusinessExceptionEnum
                                .BUSINESS_CONFIRM_ORDER_SEAT_NOT_ENOUGH
                );
            }
            markSold(
                    selected,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );
            selectedSeats.add(selected);
        }
        return selectedSeats;
    }

    private List<DailyTrainSeat> chooseSeatsByOffset(
            List<ConfirmOrderTicketReq> tickets,
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> dailyTrainSeats
    ) {
        String seatType = tickets.get(0).getSeatTypeCode();
        if (!("1".equals(seatType) || "2".equals(seatType))) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
        if (tickets.stream().anyMatch(ticket ->
                !seatType.equals(ticket.getSeatTypeCode()))) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }

        List<SeatPosition> positions = tickets.stream()
                .map(ticket -> parseSeat(ticket.getSeat()))
                .toList();
        Set<String> positionKeys = positions.stream()
                .map(SeatPosition::key)
                .collect(Collectors.toSet());
        if (positionKeys.size() != positions.size()) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }

        List<String> columns = SeatColEnum.columnsFor(seatType);
        SeatPosition first = positions.get(0);
        int firstColumnIndex = columns.indexOf(first.column());
        if (firstColumnIndex < 0) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
        List<Integer> offsets = positions.stream()
                .map(position -> {
                    int columnIndex = columns.indexOf(position.column());
                    if (columnIndex < 0) {
                        throw new BusinessException(
                                BusinessExceptionEnum
                                        .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
                        );
                    }
                    return (position.row() - first.row()) * columns.size()
                            + columnIndex - firstColumnIndex;
                })
                .toList();

        Map<Integer, List<DailyTrainSeat>> seatsByCarriage =
                dailyTrainSeats.stream()
                        .filter(seat -> seatType.equals(seat.getSeatType()))
                        .collect(Collectors.groupingBy(
                                DailyTrainSeat::getCarriageIndex,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
        for (List<DailyTrainSeat> carriageSeats : seatsByCarriage.values()) {
            carriageSeats.sort(Comparator.comparing(
                    DailyTrainSeat::getCarriageSeatIndex
            ));
            Map<Integer, DailyTrainSeat> seatByIndex = carriageSeats.stream()
                    .collect(Collectors.toMap(
                            DailyTrainSeat::getCarriageSeatIndex,
                            seat -> seat,
                            (left, right) -> left,
                            LinkedHashMap::new
                    ));
            for (DailyTrainSeat firstSeat : carriageSeats) {
                if (!first.column().equals(firstSeat.getCol())) {
                    continue;
                }
                List<DailyTrainSeat> candidateSeats = new ArrayList<>();
                boolean available = true;
                for (int i = 0; i < positions.size(); i++) {
                    DailyTrainSeat candidate = seatByIndex.get(
                            firstSeat.getCarriageSeatIndex() + offsets.get(i)
                    );
                    if (candidate == null
                            || !matchesPosition(
                            candidate,
                            firstSeat,
                            positions.get(i),
                            first
                    )
                            || !isAvailable(
                            candidate,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    )) {
                        available = false;
                        break;
                    }
                    candidateSeats.add(candidate);
                }
                if (available) {
                    candidateSeats.forEach(seat -> markSold(
                            seat,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    ));
                    return candidateSeats;
                }
            }
        }
        throw new BusinessException(
                BusinessExceptionEnum.BUSINESS_CONFIRM_ORDER_SEAT_NOT_ENOUGH
        );
    }

    private boolean matchesPosition(
            DailyTrainSeat candidate,
            DailyTrainSeat firstSeat,
            SeatPosition requested,
            SeatPosition first
    ) {
        return requested.column().equals(candidate.getCol())
                && Integer.parseInt(firstSeat.getRow())
                + requested.row() - first.row()
                == Integer.parseInt(candidate.getRow());
    }

    private boolean isAvailable(
            DailyTrainSeat seat,
            Integer startIndex,
            Integer endIndex
    ) {
        if (seat.getSell() == null || startIndex == null || endIndex == null
                || startIndex < 1 || endIndex <= startIndex) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
        int from = startIndex - 1;
        int to = endIndex - 1;
        if (to > seat.getSell().length()) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
        for (int i = from; i < to; i++) {
            if (seat.getSell().charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    private void markSold(
            DailyTrainSeat seat,
            Integer startIndex,
            Integer endIndex
    ) {
        char[] sell = seat.getSell().toCharArray();
        for (int i = startIndex - 1; i < endIndex - 1; i++) {
            sell[i] = '1';
        }
        seat.setSell(new String(sell));
    }

    private SeatPosition parseSeat(String seat) {
        if (!hasText(seat) || seat.length() < 2) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
        try {
            return new SeatPosition(
                    seat.substring(0, 1),
                    Integer.parseInt(seat.substring(1))
            );
        } catch (NumberFormatException exception) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String formatSeat(DailyTrainSeat seat) {
        return seat.getCarriageIndex()
                + "-"
                + seat.getRow()
                + seat.getCol();
    }

    private record SeatPosition(String column, int row) {
        private String key() {
            return column + row;
        }
    }

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(
            ConfirmOrderQueryReq request
    ) {
        //1. 按日期、车次和创建时间构造倒序分页查询
        Page<ConfirmOrder> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<ConfirmOrder> queryWrapper =
                new LambdaQueryWrapper<ConfirmOrder>()
                        .orderByDesc(ConfirmOrder::getDate)
                        .orderByAsc(ConfirmOrder::getTrainCode)
                        .orderByDesc(ConfirmOrder::getCreateTime);

        //2. 查询确认订单分页数据
        Page<ConfirmOrder> orderPage =
                confirmOrderMapper.selectPage(page, queryWrapper);

        //3. 转换响应对象并组装分页结果
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(
                orderPage.getRecords(),
                ConfirmOrderQueryResp.class
        );
        PageResp<ConfirmOrderQueryResp> response = new PageResp<>();
        response.setTotal(orderPage.getTotal());
        response.setList(list);
        return response;
    }

}
