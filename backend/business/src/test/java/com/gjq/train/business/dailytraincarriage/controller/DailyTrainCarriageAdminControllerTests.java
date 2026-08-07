package com.gjq.train.business.dailytraincarriage.controller;

import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageQueryReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageSaveReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageUpdateReq;
import com.gjq.train.business.dailytraincarriage.resp.DailyTrainCarriageQueryResp;
import com.gjq.train.business.dailytraincarriage.service.DailyTrainCarriageService;
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

class DailyTrainCarriageAdminControllerTests {

    private MockMvc mockMvc;

    private DailyTrainCarriageService dailyTrainCarriageService;

    @BeforeEach
    void setUp() {
        dailyTrainCarriageService = mock(DailyTrainCarriageService.class);
        DailyTrainCarriageAdminController controller =
                new DailyTrainCarriageAdminController();
        ReflectionTestUtils.setField(
                controller,
                "dailyTrainCarriageService",
                dailyTrainCarriageService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveDailyTrainCarriage() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-carriage/save")
                                .contentType(APPLICATION_JSON)
                                .content(carriageJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainCarriageService).save(argThat(
                (DailyTrainCarriageSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && Integer.valueOf(20).equals(
                                request.getRowCount()
                        )
        ));
    }

    @Test
    void shouldDeleteDailyTrainCarriage() throws Exception {
        mockMvc.perform(
                        delete("/admin/daily-train-carriage/delete/100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainCarriageService).delete(100L);
    }

    @Test
    void shouldUpdateDailyTrainCarriage() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-carriage/update")
                                .contentType(APPLICATION_JSON)
                                .content(carriageJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainCarriageService).update(argThat(
                (DailyTrainCarriageUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "2".equals(request.getSeatType())
        ));
    }

    @Test
    void shouldQueryDailyTrainCarriagePage() throws Exception {
        PageResp<DailyTrainCarriageQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(dailyTrainCarriageService.queryList(
                any(DailyTrainCarriageQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/daily-train-carriage/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-07")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(dailyTrainCarriageService).queryList(argThat(
                (DailyTrainCarriageQueryReq request) ->
                        LocalDate.of(2026, 8, 7).equals(request.getDate())
                                && "G1".equals(request.getTrainCode())
        ));
    }

    private String carriageJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "date": "2026-08-07",
                  "trainCode": "G1",
                  "index": 1,
                  "seatType": "2",
                  "rowCount": 20
                }
                """.formatted(id);
    }
}
