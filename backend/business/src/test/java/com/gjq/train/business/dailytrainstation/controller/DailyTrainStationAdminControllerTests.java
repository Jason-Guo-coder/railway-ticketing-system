package com.gjq.train.business.dailytrainstation.controller;

import com.gjq.train.business.dailytrainstation.req.DailyTrainStationQueryReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationSaveReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationUpdateReq;
import com.gjq.train.business.dailytrainstation.resp.DailyTrainStationQueryResp;
import com.gjq.train.business.dailytrainstation.service.DailyTrainStationService;
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

class DailyTrainStationAdminControllerTests {

    private MockMvc mockMvc;

    private DailyTrainStationService dailyTrainStationService;

    @BeforeEach
    void setUp() {
        dailyTrainStationService = mock(DailyTrainStationService.class);
        DailyTrainStationAdminController controller =
                new DailyTrainStationAdminController();
        ReflectionTestUtils.setField(
                controller,
                "dailyTrainStationService",
                dailyTrainStationService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveDailyTrainStation() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-station/save")
                                .contentType(APPLICATION_JSON)
                                .content(dailyTrainStationJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainStationService).save(argThat(
                (DailyTrainStationSaveReq request) ->
                        LocalDate.of(2026, 8, 7).equals(request.getDate())
                                && "G1".equals(request.getTrainCode())
        ));
    }

    @Test
    void shouldDeleteDailyTrainStation() throws Exception {
        mockMvc.perform(
                        delete("/admin/daily-train-station/delete/100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainStationService).delete(100L);
    }

    @Test
    void shouldUpdateDailyTrainStation() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train-station/update")
                                .contentType(APPLICATION_JSON)
                                .content(dailyTrainStationJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainStationService).update(argThat(
                (DailyTrainStationUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "南京南".equals(request.getName())
        ));
    }

    @Test
    void shouldQueryDailyTrainStationPage() throws Exception {
        PageResp<DailyTrainStationQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(dailyTrainStationService.queryList(
                any(DailyTrainStationQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/daily-train-station/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-07")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(dailyTrainStationService).queryList(argThat(
                (DailyTrainStationQueryReq request) ->
                        request.getPage() == 1
                                && request.getSize() == 10
                                && LocalDate.of(2026, 8, 7).equals(
                                request.getDate()
                        )
                                && "G1".equals(request.getTrainCode())
        ));
    }

    private String dailyTrainStationJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "date": "2026-08-07",
                  "trainCode": "G1",
                  "index": 2,
                  "name": "南京南",
                  "namePinyin": "nanjingnan",
                  "inTime": "10:00:00",
                  "outTime": "10:05:00",
                  "stopTime": "00:05:00",
                  "km": 300.50
                }
                """.formatted(id);
    }
}
