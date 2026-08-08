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
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.context.LoginMemberContext;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmOrderServiceImplTests {

    @Mock
    private ConfirmOrderMapper confirmOrderMapper;

    @Mock
    private DailyTrainTicketService dailyTrainTicketService;

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
        when(dailyTrainTicketService.selectByUnique(
                LocalDate.of(2026, 8, 8),
                "G1",
                "北京南",
                "上海虹桥"
        )).thenReturn(ticket);

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
        ConfirmOrderTicketReq ticket = new ConfirmOrderTicketReq();
        ticket.setPassengerId(100L);
        ticket.setPassengerType("1");
        ticket.setPassengerName("张三");
        ticket.setPassengerIdCard("110101199001010011");
        ticket.setSeatTypeCode("1");
        ticket.setSeat("A1");

        ConfirmOrderDoReq request = new ConfirmOrderDoReq();
        request.setDate(LocalDate.of(2026, 8, 8));
        request.setTrainCode("G1");
        request.setStart("北京南");
        request.setEnd("上海虹桥");
        request.setDailyTrainTicketId(300L);
        request.setTickets(List.of(ticket));
        return request;
    }
}
