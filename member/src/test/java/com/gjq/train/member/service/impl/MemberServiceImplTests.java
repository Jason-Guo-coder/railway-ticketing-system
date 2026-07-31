package com.gjq.train.member.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.member.entity.Member;
import com.gjq.train.member.mapper.MemberMapper;
import com.gjq.train.member.req.MemberRegisterReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void shouldRejectRegisteredMobile() {
        when(memberMapper.selectCount(any())).thenReturn(1L);
        MemberRegisterReq request = new MemberRegisterReq();
        request.setMobile("13900001234");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberService.register(request)
        );

        assertEquals("手机号已经注册", exception.getMessage());
        assertEquals(
                BusinessExceptionEnum.MEMBER_MOBILE_EXIST,
                exception.getExceptionEnum()
        );
        verify(memberMapper, never()).insert(any(Member.class));
    }

    @Test
    void shouldUseMybatisPlusAssignedId() throws NoSuchFieldException {
        long snowflakeId = 1950000000000000000L;
        when(memberMapper.selectCount(any())).thenReturn(0L);
        when(memberMapper.insert(any(Member.class)))
                .thenAnswer(invocation -> {
                    Member member = invocation.getArgument(0);
                    assertNull(member.getId());
                    member.setId(snowflakeId);
                    return 1;
                });
        MemberRegisterReq request = new MemberRegisterReq();
        request.setMobile("13900001234");

        long result = memberService.register(request);
        TableId tableId = Member.class
                .getDeclaredField("id")
                .getAnnotation(TableId.class);

        assertEquals(IdType.ASSIGN_ID, tableId.type());
        assertEquals(snowflakeId, result);
    }
}
