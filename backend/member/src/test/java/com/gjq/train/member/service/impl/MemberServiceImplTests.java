package com.gjq.train.member.service.impl;

import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.member.entity.Member;
import com.gjq.train.member.mapper.MemberMapper;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberSendCodeReq;
import com.gjq.train.member.resp.MemberLoginResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTests {

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(memberService, "baseMapper", memberMapper);
    }

    @Test
    void shouldCreateMemberWhenMobileDoesNotExist() {
        when(memberMapper.selectCount(any())).thenReturn(0L);
        MemberSendCodeReq request = new MemberSendCodeReq();
        request.setMobile("13900001234");

        memberService.sendCode(request);

        verify(memberMapper).insert(argThat(
                (Member member) ->
                        "13900001234".equals(member.getMobile())
        ));
    }

    @Test
    void shouldNotCreateMemberWhenMobileExists() {
        when(memberMapper.selectCount(any())).thenReturn(1L);
        MemberSendCodeReq request = new MemberSendCodeReq();
        request.setMobile("13900001234");

        memberService.sendCode(request);

        verify(memberMapper, never()).insert(any(Member.class));
    }

    @Test
    void shouldLoginWithCorrectCode() {
        Member member = new Member();
        member.setId(1L);
        member.setMobile("13900001234");
        when(memberMapper.selectOne(any())).thenReturn(member);
        MemberLoginReq request = loginRequest("8888");

        MemberLoginResp response = memberService.login(request);

        assertEquals(1L, response.getId());
        assertEquals("13900001234", response.getMobile());
    }

    @Test
    void shouldRejectLoginWhenMemberDoesNotExist() {
        when(memberMapper.selectOne(any())).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberService.login(loginRequest("8888"))
        );

        assertEquals(
                BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST,
                exception.getExceptionEnum()
        );
    }

    @Test
    void shouldRejectLoginWhenCodeIsIncorrect() {
        Member member = new Member();
        member.setId(1L);
        member.setMobile("13900001234");
        when(memberMapper.selectOne(any())).thenReturn(member);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberService.login(loginRequest("1234"))
        );

        assertEquals(
                BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR,
                exception.getExceptionEnum()
        );
    }

    private MemberLoginReq loginRequest(String code) {
        MemberLoginReq request = new MemberLoginReq();
        request.setMobile("13900001234");
        request.setCode(code);
        return request;
    }
}
