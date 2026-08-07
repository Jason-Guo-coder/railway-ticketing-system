package com.gjq.train.business.dailytrain.controller;

import com.gjq.train.business.dailytrain.req.DailyTrainQueryReq;
import com.gjq.train.business.dailytrain.req.DailyTrainSaveReq;
import com.gjq.train.business.dailytrain.req.DailyTrainUpdateReq;
import com.gjq.train.business.dailytrain.resp.DailyTrainQueryResp;
import com.gjq.train.business.dailytrain.service.DailyTrainService;
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

class DailyTrainAdminControllerTests {

    private MockMvc mockMvc;

    private DailyTrainService dailyTrainService;

    @BeforeEach
    void setUp() {
        dailyTrainService = mock(DailyTrainService.class);
        DailyTrainAdminController controller =
                new DailyTrainAdminController();
        ReflectionTestUtils.setField(
                controller,
                "dailyTrainService",
                dailyTrainService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveDailyTrain() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train/save")
                                .contentType(APPLICATION_JSON)
                                .content(dailyTrainJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainService).save(argThat(
                (DailyTrainSaveReq request) ->
                        LocalDate.of(2026, 8, 7).equals(request.getDate())
                                && "G100".equals(request.getCode())
                ));
    }

    @Test
    void shouldDeleteDailyTrain() throws Exception {
        mockMvc.perform(delete("/admin/daily-train/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainService).delete(100L);
    }

    @Test
    void shouldUpdateDailyTrain() throws Exception {
        mockMvc.perform(
                        post("/admin/daily-train/update")
                                .contentType(APPLICATION_JSON)
                                .content(dailyTrainJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainService).update(argThat(
                (DailyTrainUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "G100".equals(request.getCode())
                ));
    }

    @Test
    void shouldQueryDailyTrainPage() throws Exception {
        PageResp<DailyTrainQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(dailyTrainService.queryList(any(DailyTrainQueryReq.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/admin/daily-train/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("date", "2026-08-07")
                                .param("code", "G100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(dailyTrainService).queryList(argThat(
                (DailyTrainQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
                        && LocalDate.of(2026, 8, 7).equals(
                        request.getDate()
                )
                        && "G100".equals(request.getCode())
        ));
    }

    @Test
    void shouldGenerateDailyData() throws Exception {
        mockMvc.perform(post("/admin/daily-train/gen-daily/2026-08-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainService).generate(LocalDate.of(2026, 8, 8));
    }

    @Test
    void shouldGenerateDailyDataOnlyWhenAbsent() throws Exception {
        mockMvc.perform(post(
                        "/admin/daily-train/"
                                + "gen-daily-if-absent/2026-08-08"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dailyTrainService).generateIfAbsent(
                LocalDate.of(2026, 8, 8)
        );
    }

    private String dailyTrainJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "date": "2026-08-07",
                  "code": "G100",
                  "type": "G",
                  "start": "北京南",
                  "startPinyin": "beijingnan",
                  "startTime": "08:00:00",
                  "end": "上海虹桥",
                  "endPinyin": "shanghaihongqiao",
                  "endTime": "12:30:00"
                }
                """.formatted(id);
    }
}
