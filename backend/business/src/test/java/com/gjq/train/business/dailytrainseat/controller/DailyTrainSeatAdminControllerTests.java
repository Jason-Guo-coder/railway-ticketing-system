package com.gjq.train.business.dailytrainseat.controller;

import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatQueryReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatSaveReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatUpdateReq;
import com.gjq.train.business.dailytrainseat.resp.DailyTrainSeatQueryResp;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
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

class DailyTrainSeatAdminControllerTests {

    private MockMvc mockMvc;

    private DailyTrainSeatService dailyTrainSeatService;

    @BeforeEach
    void setUp() {
        dailyTrainSeatService = mock(DailyTrainSeatService.class);
        DailyTrainSeatAdminController controller =
                new DailyTrainSeatAdminController();
        ReflectionTestUtils.setField(
                controller,
                "dailyTrainSeatService",
                dailyTrainSeatService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveDailyTrainSeat() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-seat/save")
                                .contentType(APPLICATION_JSON)
                                .content(seatJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainSeatService).save(argThat(
                (DailyTrainSeatSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && "000".equals(request.getSell())
        ));
    }

    @Test
    void shouldDeleteDailyTrainSeat() throws Exception {
        mockMvc.perform(delete("/admin/daily-train-seat/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainSeatService).delete(100L);
    }

    @Test
    void shouldUpdateDailyTrainSeat() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-seat/update")
                                .contentType(APPLICATION_JSON)
                                .content(seatJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainSeatService).update(argThat(
                (DailyTrainSeatUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "01".equals(request.getRow())
        ));
    }

    @Test
    void shouldQueryDailyTrainSeatPage() throws Exception {
        PageResp<DailyTrainSeatQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(dailyTrainSeatService.queryList(
                any(DailyTrainSeatQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/daily-train-seat/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-07")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(dailyTrainSeatService).queryList(argThat(
                (DailyTrainSeatQueryReq request) ->
                        LocalDate.of(2026, 8, 7).equals(request.getDate())
                                && "G1".equals(request.getTrainCode())
        ));
    }

    private String seatJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "date": "2026-08-07",
                  "trainCode": "G1",
                  "carriageIndex": 1,
                  "row": "01",
                  "col": "A",
                  "seatType": "1",
                  "carriageSeatIndex": 1,
                  "sell": "000"
                }
                """.formatted(id);
    }
}
