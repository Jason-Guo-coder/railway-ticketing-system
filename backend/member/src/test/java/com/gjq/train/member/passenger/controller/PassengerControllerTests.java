package com.gjq.train.member.passenger.controller;

import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.common.resp.MemberLoginResp;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.member.passenger.req.PassengerQueryReq;
import com.gjq.train.member.passenger.req.PassengerSaveReq;
import com.gjq.train.member.passenger.resp.PassengerQueryResp;
import com.gjq.train.member.passenger.service.PassengerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PassengerControllerTests {

    private MockMvc mockMvc;
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        passengerService = mock(PassengerService.class);
        PassengerController controller = new PassengerController();
        ReflectionTestUtils.setField(
                controller,
                "passengerService",
                passengerService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        LoginMemberContext.remove();
    }

    @Test
    void shouldSavePassengerWithJsonBody() throws Exception {
        mockMvc.perform(
                        post("/passenger/save")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "张三",
                                          "idCard": "110101199001011234",
                                          "type": "1"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(passengerService).save(argThat(
                (PassengerSaveReq request) ->
                        request.getMemberId() == null
                                && "张三".equals(request.getName())
                                && "110101199001011234".equals(request.getIdCard())
                                && "1".equals(request.getType())
        ));
    }

    @Test
    void shouldQueryPassengerPageForLoginMember() throws Exception {
        MemberLoginResp member = new MemberLoginResp();
        member.setId(1L);
        LoginMemberContext.setMember(member);

        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(0L);
        pageResp.setList(List.of());
        when(passengerService.queryList(any(PassengerQueryReq.class)))
                .thenReturn(pageResp);

        mockMvc.perform(
                        get("/passenger/query-list")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(passengerService).queryList(argThat(
                (PassengerQueryReq request) ->
                        Long.valueOf(1L).equals(request.getMemberId())
                                && request.getPage() == 1
                                && request.getSize() == 10
        ));
    }

    @Test
    void shouldDeletePassenger() throws Exception {
        mockMvc.perform(delete("/passenger/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(passengerService).delete(100L);
    }

    @Test
    void shouldUpdatePassengerWithJsonBody() throws Exception {
        mockMvc.perform(
                        post("/passenger/update")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "id": "100",
                                          "name": "李四",
                                          "idCard": "110101199001011234",
                                          "type": "1"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(passengerService).update(argThat(
                (PassengerSaveReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "李四".equals(request.getName())
        ));
    }

}
