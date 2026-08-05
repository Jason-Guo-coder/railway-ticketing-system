package com.gjq.train.business.train.controller;

import com.gjq.train.business.train.req.TrainQueryReq;
import com.gjq.train.business.train.req.TrainSaveReq;
import com.gjq.train.business.train.req.TrainUpdateReq;
import com.gjq.train.business.train.resp.TrainQueryResp;
import com.gjq.train.business.train.service.TrainService;
import com.gjq.train.common.controller.ControllerExceptionHandler;
import com.gjq.train.common.resp.PageResp;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainAdminControllerTests {

    private MockMvc mockMvc;

    private TrainService trainService;

    @BeforeEach
    void setUp() {
        trainService = mock(TrainService.class);
        TrainAdminController controller = new TrainAdminController();
        ReflectionTestUtils.setField(controller, "trainService", trainService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveTrain() throws Exception {
        mockMvc.perform(
                        post("/admin/train/save")
                                .contentType(APPLICATION_JSON)
                                .content(trainJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainService).save(argThat(
                (TrainSaveReq request) ->
                        "G100".equals(request.getCode())
                                && "08:00".equals(
                                request.getStartTime().toString()
                        )
        ));
    }

    @Test
    void shouldDeleteTrain() throws Exception {
        mockMvc.perform(delete("/admin/train/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainService).delete(100L);
    }

    @Test
    void shouldUpdateTrain() throws Exception {
        mockMvc.perform(
                        post("/admin/train/update")
                                .contentType(APPLICATION_JSON)
                                .content(trainJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainService).update(argThat(
                (TrainUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "G100".equals(request.getCode())
        ));
    }

    @Test
    void shouldQueryTrainPage() throws Exception {
        PageResp<TrainQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(trainService.queryList(any(TrainQueryReq.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/admin/train/query-list")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(trainService).queryList(argThat(
                (TrainQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
        ));
    }

    @Test
    void shouldQueryAllTrains() throws Exception {
        TrainQueryResp train = new TrainQueryResp();
        train.setCode("G100");
        when(trainService.queryAll()).thenReturn(List.of(train));

        mockMvc.perform(get("/admin/train/query-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value("G100"));

        verify(trainService).queryAll();
    }

    private String trainJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
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
