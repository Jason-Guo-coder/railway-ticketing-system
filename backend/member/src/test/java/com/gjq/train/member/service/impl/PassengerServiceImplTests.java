package com.gjq.train.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.MemberLoginResp;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.member.entity.Passenger;
import com.gjq.train.member.mapper.PassengerMapper;
import com.gjq.train.member.req.PassengerQueryReq;
import com.gjq.train.member.req.PassengerSaveReq;
import com.gjq.train.member.resp.PassengerQueryResp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTests {

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @BeforeEach
    void setUp() {
        MemberLoginResp member = new MemberLoginResp();
        member.setId(1L);
        LoginMemberContext.setMember(member);
    }

    @AfterEach
    void tearDown() {
        LoginMemberContext.remove();
    }

    @Test
    void shouldInsertNewPassenger() {
        PassengerSaveReq request = passengerRequest();
        request.setMemberId(2L);

        passengerService.save(request);

        verify(passengerMapper).insert(argThat(
                (Passenger passenger) ->
                        Long.valueOf(1L).equals(
                                passenger.getMemberId()
                        )
                                && "张三".equals(passenger.getName())
                                && passenger.getCreateTime() != null
                                && passenger.getCreateTime().equals(
                                passenger.getUpdateTime()
                        )
        ));
        verify(passengerMapper, never()).updateById(any(Passenger.class));
    }

    @Test
    void shouldUpdateExistingPassenger() {
        PassengerSaveReq request = passengerRequest();
        request.setId(100L);
        request.setCreateTime(LocalDateTime.of(2026, 8, 1, 12, 0));

        passengerService.update(request);

        verify(passengerMapper).update(
                argThat(
                (Passenger passenger) ->
                        Long.valueOf(100L).equals(passenger.getId())
                                && "张三".equals(passenger.getName())
                                && Long.valueOf(1L).equals(passenger.getMemberId())
                                && passenger.getUpdateTime() != null
                ),
                any(Wrapper.class)
        );
        verify(passengerMapper, never()).insert(any(Passenger.class));
    }

    @Test
    void shouldDeletePassengerForCurrentMember() {
        passengerService.delete(100L);

        verify(passengerMapper).delete(any(Wrapper.class));
    }

    @Test
    void shouldQueryPassengerPageByMemberId() {
        PassengerQueryReq request = new PassengerQueryReq();
        request.setMemberId(1L);
        request.setPage(2);
        request.setSize(10);

        Passenger passenger = new Passenger();
        passenger.setId(100L);
        passenger.setMemberId(1L);
        passenger.setName("张三");

        when(passengerMapper.selectPage(
                any(Page.class),
                any(Wrapper.class)
        )).thenAnswer(invocation -> {
            Page<Passenger> page = invocation.getArgument(0);
            page.setRecords(List.of(passenger));
            page.setTotal(11);
            return page;
        });

        PageResp<PassengerQueryResp> result = passengerService.queryList(
                request
        );

        assertEquals(11L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals(100L, result.getList().get(0).getId());
        ArgumentCaptor<Page<Passenger>> pageCaptor =
                ArgumentCaptor.forClass(Page.class);
        verify(passengerMapper).selectPage(
                pageCaptor.capture(),
                any(Wrapper.class)
        );

        assertEquals(2, pageCaptor.getValue().getCurrent());
        assertEquals(10, pageCaptor.getValue().getSize());
    }

    private PassengerSaveReq passengerRequest() {
        PassengerSaveReq request = new PassengerSaveReq();
        request.setName("张三");
        request.setMemberId(1L);
        request.setIdCard("110101199001011234");
        request.setType("1");
        return request;
    }
}
