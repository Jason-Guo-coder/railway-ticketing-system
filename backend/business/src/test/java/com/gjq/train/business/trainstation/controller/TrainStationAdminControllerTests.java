package com.gjq.train.business.trainstation.controller;

import com.gjq.train.business.trainstation.req.TrainStationQueryReq;
import com.gjq.train.business.trainstation.req.TrainStationSaveReq;
import com.gjq.train.business.trainstation.req.TrainStationUpdateReq;
import com.gjq.train.business.trainstation.resp.TrainStationQueryResp;
import com.gjq.train.business.trainstation.service.TrainStationService;
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

class TrainStationAdminControllerTests {

    private MockMvc mockMvc;

    private TrainStationService trainStationService;

    @BeforeEach
    void setUp() {
        trainStationService = mock(TrainStationService.class);
        TrainStationAdminController controller =
                new TrainStationAdminController();
        ReflectionTestUtils.setField(
                controller,
                "trainStationService",
                trainStationService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveTrainStation() throws Exception {
        mockMvc.perform(
                        post("/admin/train-station/save")
                                .contentType(APPLICATION_JSON)
                                .content(trainStationJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainStationService).save(argThat(
                (TrainStationSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && Integer.valueOf(2).equals(
                                request.getIndex()
                        )
                ));
    }

    @Test
    void shouldDeleteTrainStation() throws Exception {
        mockMvc.perform(delete("/admin/train-station/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainStationService).delete(100L);
    }

    @Test
    void shouldUpdateTrainStation() throws Exception {
        mockMvc.perform(
                        post("/admin/train-station/update")
                                .contentType(APPLICATION_JSON)
                                .content(trainStationJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainStationService).update(argThat(
                (TrainStationUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "南京南".equals(request.getName())
        ));
    }

    @Test
    void shouldQueryTrainStationPage() throws Exception {
        PageResp<TrainStationQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(trainStationService.queryList(
                any(TrainStationQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/train-station/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(trainStationService).queryList(argThat(
                (TrainStationQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
                        && "G1".equals(request.getTrainCode())
        ));
    }

    private String trainStationJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
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
