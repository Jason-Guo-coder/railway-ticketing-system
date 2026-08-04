package com.gjq.train.member.service.impl;

import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.MemberLoginResp;
import com.gjq.train.member.entity.Passenger;
import com.gjq.train.member.mapper.PassengerMapper;
import com.gjq.train.member.req.PassengerSaveReq;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

        passengerService.save(request);

        verify(passengerMapper).updateById(argThat(
                (Passenger passenger) ->
                        Long.valueOf(100L).equals(passenger.getId())
                                && "张三".equals(passenger.getName())
                                && passenger.getUpdateTime() != null
        ));
        verify(passengerMapper, never()).insert(any(Passenger.class));
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
