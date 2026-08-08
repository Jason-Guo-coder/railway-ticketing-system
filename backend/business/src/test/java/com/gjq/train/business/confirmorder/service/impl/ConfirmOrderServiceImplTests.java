package com.gjq.train.business.confirmorder.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.business.confirmorder.enums.ConfirmOrderStatusEnum;
import com.gjq.train.business.confirmorder.entity.ConfirmOrder;
import com.gjq.train.business.confirmorder.mapper.ConfirmOrderMapper;
import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderTicketReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.business.confirmorder.service.ConfirmOrderTransactionService;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.resp.MemberLoginResp;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.AfterEach;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmOrderServiceImplTests {

    @Mock
    private ConfirmOrderMapper confirmOrderMapper;

    @Mock
    private DailyTrainTicketService dailyTrainTicketService;

    @Mock
    private DailyTrainSeatService dailyTrainSeatService;

    @Mock
    private ConfirmOrderTransactionService confirmOrderTransactionService;

    @InjectMocks
    private ConfirmOrderServiceImpl confirmOrderService;

    @AfterEach
    void tearDown() {
        LoginMemberContext.remove();
    }

    @Test
    void shouldSaveInitialOrderThenQueryRealInventory() {
        MemberLoginResp member = new MemberLoginResp();
        member.setId(900L);
        LoginMemberContext.setMember(member);
        DailyTrainTicket ticket = new DailyTrainTicket();
        ticket.setId(300L);
        ticket.setStartIndex(1);
        ticket.setEndIndex(2);
        ticket.setYdz(10);
        ticket.setEdz(10);
        ticket.setRw(10);
        ticket.setYw(10);
        when(dailyTrainTicketService.selectByUnique(
                LocalDate.of(2026, 8, 8),
                "G1",
                "北京南",
                "上海虹桥"
        )).thenReturn(ticket);
        when(dailyTrainSeatService.list(any(Wrapper.class)))
                .thenReturn(List.of(seat(1, "01", "A", 1, "0")));

        confirmOrderService.doConfirm(confirmOrderRequest());

        ArgumentCaptor<ConfirmOrder> captor =
                ArgumentCaptor.forClass(ConfirmOrder.class);
        verify(confirmOrderMapper).insert(captor.capture());
        ConfirmOrder order = captor.getValue();
        assertEquals(900L, order.getMemberId());
        assertEquals(LocalDate.of(2026, 8, 8), order.getDate());
        assertEquals("G1", order.getTrainCode());
        assertEquals("300", order.getDailyTrainTicketId().toString());
        assertEquals(
                ConfirmOrderStatusEnum.INIT.getCode(),
                order.getStatus()
        );
        assertEquals(order.getCreateTime(), order.getUpdateTime());
        assertEquals(
                "A1",
                JSONUtil.parseArray(order.getTickets())
                        .getJSONObject(0)
                        .getStr("seat")
        );

        InOrder inOrder = inOrder(
                confirmOrderMapper,
                dailyTrainTicketService
        );
        inOrder.verify(confirmOrderMapper).insert(any(ConfirmOrder.class));
        inOrder.verify(dailyTrainTicketService).selectByUnique(
                LocalDate.of(2026, 8, 8),
                "G1",
                "北京南",
                "上海虹桥"
        );
    }

    @Test
    void shouldAllocateSeatsInCarriageOrderWhenSeatIsNotSelected() {
        DailyTrainTicket inventory = inventory(2);
        when(dailyTrainTicketService.selectByUnique(
                any(), any(), any(), any()
        )).thenReturn(inventory);
        when(dailyTrainSeatService.list(any(Wrapper.class))).thenReturn(
                List.of(
                        seat(1, "01", "A", 1, "0"),
                        seat(1, "01", "C", 2, "0")
                )
        );

        ConfirmOrderDoReq request = confirmOrderRequest();
        request.getTickets().get(0).setSeat(null);
        ConfirmOrderTicketReq second = ticket("1", null);
        request.setTickets(List.of(request.getTickets().get(0), second));
        LoginMemberContext.setMember(member());

        confirmOrderService.doConfirm(request);

        assertEquals("1-01A", request.getTickets().get(0).getSeat());
        assertEquals("1-01C", request.getTickets().get(1).getSeat());
    }

    @Test
    void shouldUseSeatOffsetForSelectedSeats() {
        DailyTrainTicket inventory = inventory(2);
        when(dailyTrainTicketService.selectByUnique(
                any(), any(), any(), any()
        )).thenReturn(inventory);
        when(dailyTrainSeatService.list(any(Wrapper.class))).thenReturn(
                firstClassSeats(2)
        );

        ConfirmOrderDoReq request = confirmOrderRequest();
        request.getTickets().get(0).setSeat("C1");
        ConfirmOrderTicketReq second = ticket("1", "D2");
        request.setTickets(List.of(request.getTickets().get(0), second));
        LoginMemberContext.setMember(member());

        confirmOrderService.doConfirm(request);

        assertEquals("1-01C", request.getTickets().get(0).getSeat());
        assertEquals("1-02D", request.getTickets().get(1).getSeat());
    }

    @Test
    void shouldTryNextCandidateWhenFirstSelectedSeatIsSold() {
        DailyTrainTicket inventory = inventory(2);
        when(dailyTrainTicketService.selectByUnique(
                any(), any(), any(), any()
        )).thenReturn(inventory);
        List<DailyTrainSeat> seats = firstClassSeats(3);
        seats.get(1).setSell("1");
        when(dailyTrainSeatService.list(any(Wrapper.class)))
                .thenReturn(seats);

        ConfirmOrderDoReq request = confirmOrderRequest();
        request.getTickets().get(0).setSeat("C1");
        ConfirmOrderTicketReq second = ticket("1", "D2");
        request.setTickets(List.of(request.getTickets().get(0), second));
        LoginMemberContext.setMember(member());

        confirmOrderService.doConfirm(request);

        assertEquals("1-02C", request.getTickets().get(0).getSeat());
        assertEquals("1-03D", request.getTickets().get(1).getSeat());
    }

    @Test
    void shouldRejectWhenTemporaryInventoryRunsOut() {
        DailyTrainTicket inventory = inventory(1);
        when(dailyTrainTicketService.selectByUnique(
                any(), any(), any(), any()
        )).thenReturn(inventory);
        ConfirmOrderDoReq request = confirmOrderRequest();
        request.getTickets().get(0).setSeat(null);
        request.setTickets(List.of(
                request.getTickets().get(0),
                ticket("1", null)
        ));
        LoginMemberContext.setMember(member());

        assertThrows(
                BusinessException.class,
                () -> confirmOrderService.doConfirm(request)
        );
        verify(dailyTrainSeatService, never()).list(any(Wrapper.class));
    }

    @Test
    void shouldQueryConfirmOrderPage() {
        ConfirmOrderQueryReq request = new ConfirmOrderQueryReq();
        request.setPage(2);
        request.setSize(10);

        ConfirmOrder order = new ConfirmOrder();
        order.setId(100L);
        order.setMemberId(200L);
        order.setDate(LocalDate.of(2026, 8, 8));
        order.setTrainCode("G1");
        order.setTickets("[]");
        order.setStatus("I");
        when(confirmOrderMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<ConfirmOrder> page = invocation.getArgument(0);
            page.setRecords(List.of(order));
            page.setTotal(11);
            return page;
        });

        PageResp<ConfirmOrderQueryResp> response =
                confirmOrderService.queryList(request);

        assertEquals(11L, response.getTotal());
        assertEquals(1, response.getList().size());
        assertEquals(100L, response.getList().get(0).getId());
        assertEquals("G1", response.getList().get(0).getTrainCode());
        assertEquals("[]", response.getList().get(0).getTickets());
        assertEquals("I", response.getList().get(0).getStatus());
    }

    private ConfirmOrderDoReq confirmOrderRequest() {
        ConfirmOrderTicketReq ticket = ticket("1", "A1");

        ConfirmOrderDoReq request = new ConfirmOrderDoReq();
        request.setDate(LocalDate.of(2026, 8, 8));
        request.setTrainCode("G1");
        request.setStart("北京南");
        request.setEnd("上海虹桥");
        request.setDailyTrainTicketId(300L);
        request.setTickets(List.of(ticket));
        return request;
    }

    private ConfirmOrderTicketReq ticket(String seatTypeCode, String seat) {
        ConfirmOrderTicketReq ticket = new ConfirmOrderTicketReq();
        ticket.setPassengerId(100L);
        ticket.setPassengerType("1");
        ticket.setPassengerName("张三");
        ticket.setPassengerIdCard("110101199001010011");
        ticket.setSeatTypeCode(seatTypeCode);
        ticket.setSeat(seat);
        return ticket;
    }

    private DailyTrainTicket inventory(int ydz) {
        DailyTrainTicket ticket = new DailyTrainTicket();
        ticket.setId(300L);
        ticket.setStartIndex(1);
        ticket.setEndIndex(2);
        ticket.setYdz(ydz);
        ticket.setEdz(10);
        ticket.setRw(10);
        ticket.setYw(10);
        return ticket;
    }

    private List<DailyTrainSeat> firstClassSeats(int rows) {
        List<DailyTrainSeat> seats = new ArrayList<>();
        String[] columns = {"A", "C", "D", "F"};
        int index = 1;
        for (int row = 1; row <= rows; row++) {
            for (String column : columns) {
                seats.add(seat(
                        1,
                        "%02d".formatted(row),
                        column,
                        index++,
                        "0"
                ));
            }
        }
        return seats;
    }

    private DailyTrainSeat seat(
            int carriageIndex,
            String row,
            String col,
            int carriageSeatIndex,
            String sell
    ) {
        DailyTrainSeat seat = new DailyTrainSeat();
        seat.setDate(LocalDate.of(2026, 8, 8));
        seat.setTrainCode("G1");
        seat.setCarriageIndex(carriageIndex);
        seat.setRow(row);
        seat.setCol(col);
        seat.setSeatType("1");
        seat.setCarriageSeatIndex(carriageSeatIndex);
        seat.setSell(sell);
        return seat;
    }

    private MemberLoginResp member() {
        MemberLoginResp member = new MemberLoginResp();
        member.setId(900L);
        return member;
    }
}
