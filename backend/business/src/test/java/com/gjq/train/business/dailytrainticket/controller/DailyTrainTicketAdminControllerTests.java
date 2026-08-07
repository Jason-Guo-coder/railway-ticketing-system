package com.gjq.train.business.dailytrainticket.controller;

import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketSaveReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketUpdateReq;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DailyTrainTicketAdminControllerTests {

    private MockMvc mockMvc;

    private DailyTrainTicketService dailyTrainTicketService;

    @BeforeEach
    void setUp() {
        dailyTrainTicketService = mock(DailyTrainTicketService.class);
        DailyTrainTicketAdminController controller =
                new DailyTrainTicketAdminController();
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
    void shouldSaveDailyTrainTicket() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-ticket/save")
                                .contentType(APPLICATION_JSON)
                                .content(ticketJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainTicketService).save(argThat(
                (DailyTrainTicketSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && "北京南".equals(request.getStart())
                                && Integer.valueOf(120).equals(
                                request.getYdz()
                        )
        ));
    }

    @Test
    void shouldDeleteDailyTrainTicket() throws Exception {
        mockMvc.perform(
                        delete("/admin/daily-train-ticket/delete/100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainTicketService).delete(100L);
    }

    @Test
    void shouldUpdateDailyTrainTicket() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-ticket/update")
                                .contentType(APPLICATION_JSON)
                                .content(ticketJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainTicketService).update(argThat(
                (DailyTrainTicketUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "上海虹桥".equals(request.getEnd())
        ));
    }

    @Test
    void shouldQueryDailyTrainTicketPage() throws Exception {
        PageResp<DailyTrainTicketQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(dailyTrainTicketService.queryList(
                any(DailyTrainTicketQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/daily-train-ticket/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-08")
                                .param("trainCode", "G1")
                                .param("start", "北京南")
                                .param("end", "上海虹桥")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(dailyTrainTicketService).queryList(argThat(
                (DailyTrainTicketQueryReq request) ->
                        LocalDate.of(2026, 8, 8).equals(request.getDate())
                                && "G1".equals(request.getTrainCode())
                                && "北京南".equals(request.getStart())
                                && "上海虹桥".equals(request.getEnd())
        ));
    }

    private String ticketJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "date": "2026-08-08",
                  "trainCode": "G1",
                  "start": "北京南",
                  "startPinyin": "beijingnan",
                  "startTime": "07:00:00",
                  "startIndex": 1,
                  "end": "上海虹桥",
                  "endPinyin": "shanghaihongqiao",
                  "endTime": "11:30:00",
                  "endIndex": 5,
                  "ydz": 120,
                  "ydzPrice": 933.00,
                  "edz": 500,
                  "edzPrice": 553.00,
                  "rw": -1,
                  "rwPrice": 0.00,
                  "yw": -1,
                  "ywPrice": 0.00
                }
                """.formatted(id);
    }
}
