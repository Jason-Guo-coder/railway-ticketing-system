package com.gjq.train.business.confirmorder.service;

import cn.hutool.json.JSONUtil;
import com.gjq.train.business.confirmorder.entity.ConfirmOrder;
import com.gjq.train.business.confirmorder.enums.ConfirmOrderStatusEnum;
import com.gjq.train.business.confirmorder.mapper.ConfirmOrderMapper;
import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderTicketReq;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.mapper.DailyTrainSeatMapper;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.mapper.DailyTrainTicketMapper;
import com.gjq.train.business.memberticket.entity.MemberTicket;
import com.gjq.train.business.memberticket.mapper.MemberTicketMapper;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 确认订单最终落库事务。
 *
 * <p>初始确认订单在外层先保存，这里只包住座位、余票、会员车票和订单成功状态。</p>
 */
@Service
public class ConfirmOrderTransactionService {

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    private MemberTicketMapper memberTicketMapper;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Transactional(rollbackFor = Exception.class)
    public void finish(
            ConfirmOrder confirmOrder,
            ConfirmOrderDoReq request,
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> selectedSeats
    ) {
        LocalDateTime now = LocalDateTime.now();

        //1. 以旧sell为条件更新座位，避免覆盖并发请求已经售出的座位
        for (DailyTrainSeat seat : selectedSeats) {
            String newSell = seat.getSell();
            String oldSell = restoreSelectedSegment(
                    newSell,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );
            int updated = dailyTrainSeatMapper.updateSellIfMatch(
                    seat.getId(),
                    oldSell,
                    newSell,
                    now
            );
            if (updated != 1) {
                throw new BusinessException(
                        BusinessExceptionEnum
                                .BUSINESS_CONFIRM_ORDER_SEAT_NOT_ENOUGH
                );
            }
        }

        //2. 一次原子扣减本次订单涉及的各类余票
        int ydz = countBySeatType(request, "1");
        int edz = countBySeatType(request, "2");
        int rw = countBySeatType(request, "3");
        int yw = countBySeatType(request, "4");
        if (dailyTrainTicketMapper.deductInventory(
                dailyTrainTicket.getId(),
                ydz,
                edz,
                rw,
                yw
        ) != 1) {
            throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_TICKET_NOT_ENOUGH
            );
        }

        //3. 为当前会员逐张保存已购买车票
        for (int i = 0; i < request.getTickets().size(); i++) {
            ConfirmOrderTicketReq ticket = request.getTickets().get(i);
            DailyTrainSeat seat = selectedSeats.get(i);
            MemberTicket memberTicket = new MemberTicket();
            memberTicket.setMemberId(confirmOrder.getMemberId());
            memberTicket.setPassengerId(ticket.getPassengerId());
            memberTicket.setDate(request.getDate());
            memberTicket.setTrainCode(request.getTrainCode());
            memberTicket.setStart(request.getStart());
            memberTicket.setEnd(request.getEnd());
            memberTicket.setCarriageIndex(seat.getCarriageIndex());
            memberTicket.setRow(seat.getRow());
            memberTicket.setCol(seat.getCol());
            memberTicket.setSeatType(ticket.getSeatTypeCode());
            memberTicket.setSeat(ticket.getSeat());
            memberTicket.setPassengerType(ticket.getPassengerType());
            memberTicket.setPassengerName(ticket.getPassengerName());
            memberTicket.setPassengerIdCard(ticket.getPassengerIdCard());
            memberTicket.setPrice(priceOf(dailyTrainTicket, ticket));
            memberTicket.setCreateTime(now);
            memberTicket.setUpdateTime(now);
            memberTicketMapper.insert(memberTicket);
        }

        //4. 保存后端最终座位并把确认订单置为成功
        confirmOrder.setTickets(JSONUtil.toJsonStr(request.getTickets()));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
        confirmOrder.setUpdateTime(now);
        confirmOrderMapper.updateById(confirmOrder);
    }

    private int countBySeatType(
            ConfirmOrderDoReq request,
            String seatType
    ) {
        return (int) request.getTickets().stream()
                .filter(ticket -> seatType.equals(ticket.getSeatTypeCode()))
                .count();
    }

    private String restoreSelectedSegment(
            String newSell,
            Integer startIndex,
            Integer endIndex
    ) {
        char[] oldSell = newSell.toCharArray();
        for (int i = startIndex - 1; i < endIndex - 1; i++) {
            oldSell[i] = '0';
        }
        return new String(oldSell);
    }

    private BigDecimal priceOf(
            DailyTrainTicket dailyTrainTicket,
            ConfirmOrderTicketReq ticket
    ) {
        return switch (ticket.getSeatTypeCode()) {
            case "1" -> dailyTrainTicket.getYdzPrice();
            case "2" -> dailyTrainTicket.getEdzPrice();
            case "3" -> dailyTrainTicket.getRwPrice();
            case "4" -> dailyTrainTicket.getYwPrice();
            default -> throw new BusinessException(
                    BusinessExceptionEnum
                            .BUSINESS_CONFIRM_ORDER_SEAT_SELECTION_INVALID
            );
        };
    }
}
