package com.gjq.train.member.member.controller;

import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.member.member.req.MemberLoginReq;
import com.gjq.train.member.member.resp.MemberLoginResp;
import com.gjq.train.member.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
    void shouldRegisterWithJsonBody() throws Exception {
        String mobile = "13800001234";

        mockMvc.perform(
                        post("/member/register")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "mobile": "13800001234"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").doesNotExist());

        verify(memberService).register(argThat(
                request -> mobile.equals(request.getMobile())
        ));
    }

    @Test
    void shouldReturnMobileValidationMessageForMissingParameter()
            throws Exception {
        mockMvc.perform(
                        post("/member/register")
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("手机号不能为空"));

        verifyNoInteractions(memberService);
    }

    @Test
    void shouldReturnMobileFormatMessageForInvalidMobile()
            throws Exception {
        mockMvc.perform(
                        post("/member/register")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "mobile": "23800001234"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("手机号码格式错误"));

        verifyNoInteractions(memberService);
    }

    @Test
    void shouldLoginWithJsonBody() throws Exception {
        MemberLoginResp response = new MemberLoginResp();
        response.setId(1L);
        response.setMobile("13800001234");
        when(memberService.login(argThat(
                request -> "13800001234".equals(request.getMobile())
                        && "8888".equals(request.getCode())
        ))).thenReturn(response);

        mockMvc.perform(
                        post("/member/login")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "mobile": "13800001234",
                                          "code": "8888"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.id").value(1))
                .andExpect(jsonPath("$.content.mobile")
                        .value("13800001234"));

        verify(memberService).login(argThat(
                (MemberLoginReq request) ->
                        "13800001234".equals(request.getMobile())
                                && "8888".equals(request.getCode())
        ));
    }

    @Test
    void shouldRejectLoginWithoutCode() throws Exception {
        mockMvc.perform(
                        post("/member/login")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "mobile": "13800001234"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("验证码不能为空"));

        verifyNoInteractions(memberService);
    }
}
