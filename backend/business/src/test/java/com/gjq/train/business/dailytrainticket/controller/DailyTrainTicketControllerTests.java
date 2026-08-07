package com.gjq.train.business.dailytrainticket.controller;

import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.resp.DailyTrainTicketQueryResp;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.common.resp.PageResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DailyTrainTicketControllerTests {

    private MockMvc mockMvc;

    private DailyTrainTicketService dailyTrainTicketService;

    @BeforeEach
    void setUp() {
        dailyTrainTicketService = mock(DailyTrainTicketService.class);
        DailyTrainTicketController controller =
                new DailyTrainTicketController();
        ReflectionTestUtils.setField(
                controller,
                "dailyTrainTicketService",
                dailyTrainTicketService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldQueryTicketsForMember() throws Exception {
        PageResp<DailyTrainTicketQueryResp> response = new PageResp<>();
        response.setTotal(1L);
        response.setList(List.of(new DailyTrainTicketQueryResp()));
        when(dailyTrainTicketService.queryList(
                org.mockito.ArgumentMatchers.any(
                        DailyTrainTicketQueryReq.class
                )
        )).thenReturn(response);

        mockMvc.perform(
                        get("/daily-train-ticket/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-14")
                                .param("start", "北京南")
                                .param("end", "上海虹桥")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.total").value(1));

        verify(dailyTrainTicketService).queryList(argThat(
                request -> LocalDate.of(2026, 8, 14)
                        .equals(request.getDate())
                        && "北京南".equals(request.getStart())
                        && "上海虹桥".equals(request.getEnd())
                        && request.getTrainCode() == null
        ));
    }
}
