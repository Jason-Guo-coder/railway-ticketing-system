package com.gjq.train.member.controller;

import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.member.req.MemberRegisterReq;
import com.gjq.train.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTests {

    private MockMvc mockMvc;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        MemberController controller = new MemberController();
        ReflectionTestUtils.setField(
                controller,
                "memberService",
                memberService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldRegisterWithQueryParameter() throws Exception {
        String mobile = "13800001234";
        long memberId = 1950000000000000000L;
        when(memberService.register(any(MemberRegisterReq.class)))
                .thenReturn(memberId);

        mockMvc.perform(
                        post("/member/register")
                                .param("mobile", mobile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").value(memberId));

        verify(memberService).register(argThat(
                request -> mobile.equals(request.getMobile())
        ));
    }

    @Test
    void shouldReturnMobileValidationMessageForMissingParameter()
            throws Exception {
        mockMvc.perform(post("/member/register"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("手机号不能为空"));

        verifyNoInteractions(memberService);
    }
}
