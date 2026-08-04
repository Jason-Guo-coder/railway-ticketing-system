package com.gjq.train.member.service.impl;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.member.entity.Member;
import com.gjq.train.member.mapper.MemberMapper;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberRegisterReq;
import com.gjq.train.member.resp.MemberLoginResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTests {

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(memberService, "baseMapper", memberMapper);
        ReflectionTestUtils.setField(
                memberService,
                "jwtSecret",
                "member-service-test-secret"
        );
    }

    @Test
    void shouldCreateMemberWhenMobileDoesNotExist() {
        when(memberMapper.selectCount(any())).thenReturn(0L);
        MemberRegisterReq request = new MemberRegisterReq();
        request.setMobile("13900001234");

        memberService.register(request);

        verify(memberMapper).insert(argThat(
                (Member member) ->
                        "13900001234".equals(member.getMobile())
        ));
    }

    @Test
    void shouldNotCreateMemberWhenMobileExists() {
        when(memberMapper.selectCount(any())).thenReturn(1L);
        MemberRegisterReq request = new MemberRegisterReq();
        request.setMobile("13900001234");

        memberService.register(request);

        verify(memberMapper, never()).insert(any(Member.class));
    }

    @Test
    void shouldLoginWithCorrectCode() {
        Member member = new Member();
        member.setId(1L);
        member.setMobile("13900001234");
        when(memberMapper.selectList(any())).thenReturn(List.of(member));
        MemberLoginReq request = loginRequest("8888");

        MemberLoginResp response = memberService.login(request);

        assertEquals(1L, response.getId());
        assertEquals("13900001234", response.getMobile());
        JWT jwt = JWTUtil.parseToken(response.getToken()).setKey(
                "member-service-test-secret".getBytes(
                        StandardCharsets.UTF_8
                )
        );
        assertTrue(jwt.validate(0));
        assertEquals(1L, ((Number) jwt.getPayload("id")).longValue());
        assertEquals("13900001234", jwt.getPayload("mobile"));
    }

    @Test
    void shouldRejectLoginWhenMemberDoesNotExist() {
        when(memberMapper.selectList(any())).thenReturn(List.of());

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
        when(memberMapper.selectList(any())).thenReturn(List.of(member));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberService.login(loginRequest("1234"))
        );

        assertEquals(
                BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR,
                exception.getExceptionEnum()
        );
    }

    @Test
    void shouldLoginWhenMobileHasDuplicateRecords() {
        Member first = new Member();
        first.setId(1L);
        first.setMobile("13900001234");
        Member second = new Member();
        second.setId(2L);
        second.setMobile("13900001234");
        when(memberMapper.selectList(any())).thenReturn(List.of(first, second));

        MemberLoginResp response = memberService.login(loginRequest("8888"));

        assertEquals(1L, response.getId());
        assertEquals("13900001234", response.getMobile());
    }

    private MemberLoginReq loginRequest(String code) {
        MemberLoginReq request = new MemberLoginReq();
        request.setMobile("13900001234");
        request.setCode(code);
        return request;
    }
}
