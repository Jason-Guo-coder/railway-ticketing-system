package com.gjq.train.business.traincarriage.controller;

import com.gjq.train.business.traincarriage.req.TrainCarriageQueryReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageSaveReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageUpdateReq;
import com.gjq.train.business.traincarriage.resp.TrainCarriageQueryResp;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
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

class TrainCarriageAdminControllerTests {

    private MockMvc mockMvc;

    private TrainCarriageService trainCarriageService;

    @BeforeEach
    void setUp() {
        trainCarriageService = mock(TrainCarriageService.class);
        TrainCarriageAdminController controller =
                new TrainCarriageAdminController();
        ReflectionTestUtils.setField(
                controller,
                "trainCarriageService",
                trainCarriageService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveTrainCarriage() throws Exception {
        mockMvc.perform(
                        post("/admin/train-carriage/save")
                                .contentType(APPLICATION_JSON)
                                .content(carriageJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainCarriageService).save(argThat(
                (TrainCarriageSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && Integer.valueOf(20).equals(
                                request.getRowCount()
                        )
        ));
    }

    @Test
    void shouldDeleteTrainCarriage() throws Exception {
        mockMvc.perform(delete("/admin/train-carriage/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainCarriageService).delete(100L);
    }

    @Test
    void shouldUpdateTrainCarriage() throws Exception {
        mockMvc.perform(
                        post("/admin/train-carriage/update")
                                .contentType(APPLICATION_JSON)
                                .content(carriageJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainCarriageService).update(argThat(
                (TrainCarriageUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "2".equals(request.getSeatType())
        ));
    }

    @Test
    void shouldQueryTrainCarriagePage() throws Exception {
        PageResp<TrainCarriageQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(trainCarriageService.queryList(
                any(TrainCarriageQueryReq.class)
        )).thenReturn(response);

        mockMvc.perform(
                        get("/admin/train-carriage/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(trainCarriageService).queryList(argThat(
                (TrainCarriageQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
                        && "G1".equals(request.getTrainCode())
        ));
    }

    private String carriageJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "trainCode": "G1",
                  "index": 1,
                  "seatType": "2",
                  "rowCount": 20
                }
                """.formatted(id);
    }
}
