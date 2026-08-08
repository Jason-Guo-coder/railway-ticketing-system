package com.gjq.train.business.confirmorder.controller;

import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmOrderControllerTests {

    private MockMvc mockMvc;

    private ConfirmOrderService confirmOrderService;

    @BeforeEach
    void setUp() {
        confirmOrderService = mock(ConfirmOrderService.class);
        ConfirmOrderController controller = new ConfirmOrderController();
        ReflectionTestUtils.setField(
                controller,
                "confirmOrderService",
                confirmOrderService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldAcceptConfirmOrderWithoutMemberId() throws Exception {
        mockMvc.perform(
                        post("/confirm-order/do")
                                .contentType(APPLICATION_JSON)
                                .content(confirmOrderJson())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(confirmOrderService).doConfirm(argThat(
                (ConfirmOrderDoReq request) ->
                        "G1".equals(request.getTrainCode())
                                && request.getTickets().size() == 1
        ));
    }

    @Test
    void shouldRejectEmptyTickets() throws Exception {
        mockMvc.perform(
                        post("/confirm-order/do")
                                .contentType(APPLICATION_JSON)
                                .content(emptyConfirmOrderJson())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(confirmOrderService, never()).doConfirm(
                org.mockito.ArgumentMatchers.any(ConfirmOrderDoReq.class)
        );
    }

    private String confirmOrderJson() {
        return confirmOrderJson("""
                [{
                  "passengerId": "100",
                  "passengerType": "1",
                  "passengerName": "张三",
                  "passengerIdCard": "110101199001010011",
                  "seatTypeCode": "1",
                  "seat": "A1"
                }]
                """);
    }

    private String emptyConfirmOrderJson() {
        return confirmOrderJson("[]");
    }

    private String confirmOrderJson(String tickets) {
        return """
                {
                  "date": "2026-08-08",
                  "trainCode": "G1",
                  "start": "北京南",
                  "end": "上海虹桥",
                  "dailyTrainTicketId": "300",
                  "tickets": %s
                }
                """.formatted(tickets);
    }
}
