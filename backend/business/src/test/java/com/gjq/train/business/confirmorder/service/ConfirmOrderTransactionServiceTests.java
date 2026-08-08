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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmOrderTransactionServiceTests {

    @Mock
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Mock
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Mock
    private MemberTicketMapper memberTicketMapper;

    @Mock
    private ConfirmOrderMapper confirmOrderMapper;

    @InjectMocks
    private ConfirmOrderTransactionService transactionService;

    @Test
    void shouldPersistSeatInventoryMemberTicketAndSuccessOrder() {
        ConfirmOrder order = new ConfirmOrder();
        order.setId(700L);
        order.setMemberId(900L);

        ConfirmOrderTicketReq ticket = new ConfirmOrderTicketReq();
        ticket.setPassengerId(100L);
        ticket.setPassengerType("1");
        ticket.setPassengerName("张三");
        ticket.setPassengerIdCard("110101199001010011");
        ticket.setSeatTypeCode("1");
        ticket.setSeat("1-01A");

        ConfirmOrderDoReq request = new ConfirmOrderDoReq();
        request.setDate(LocalDate.of(2026, 8, 8));
        request.setTrainCode("G1");
        request.setStart("北京南");
        request.setEnd("上海虹桥");
        request.setTickets(List.of(ticket));

        DailyTrainTicket inventory = new DailyTrainTicket();
        inventory.setId(300L);
        inventory.setStartIndex(1);
        inventory.setEndIndex(2);
        inventory.setYdzPrice(new BigDecimal("553.00"));

        DailyTrainSeat seat = new DailyTrainSeat();
        seat.setId(500L);
        seat.setCarriageIndex(1);
        seat.setRow("01");
        seat.setCol("A");
        seat.setSeatType("1");
        seat.setSell("1");

        when(dailyTrainSeatMapper.updateSellIfMatch(
                eq(500L),
                eq("0"),
                eq("1"),
                any(LocalDateTime.class)
        )).thenReturn(1);
        when(dailyTrainTicketMapper.deductInventory(300L, 1, 0, 0, 0))
                .thenReturn(1);

        transactionService.finish(order, request, inventory, List.of(seat));

        ArgumentCaptor<MemberTicket> memberTicketCaptor =
                ArgumentCaptor.forClass(MemberTicket.class);
        verify(memberTicketMapper).insert(memberTicketCaptor.capture());
        MemberTicket memberTicket = memberTicketCaptor.getValue();
        assertEquals(900L, memberTicket.getMemberId());
        assertEquals(100L, memberTicket.getPassengerId());
        assertEquals("1-01A", memberTicket.getSeat());
        assertEquals(new BigDecimal("553.00"), memberTicket.getPrice());

        verify(confirmOrderMapper).updateById(order);
        assertEquals(
                ConfirmOrderStatusEnum.SUCCESS.getCode(),
                order.getStatus()
        );
        assertEquals(
                "1-01A",
                JSONUtil.parseArray(order.getTickets())
                        .getJSONObject(0)
                        .getStr("seat")
        );
    }
}
