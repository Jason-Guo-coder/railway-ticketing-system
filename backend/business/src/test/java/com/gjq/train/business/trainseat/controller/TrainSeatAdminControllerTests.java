package com.gjq.train.business.trainseat.controller;

import com.gjq.train.business.trainseat.req.TrainSeatQueryReq;
import com.gjq.train.business.trainseat.req.TrainSeatSaveReq;
import com.gjq.train.business.trainseat.req.TrainSeatUpdateReq;
import com.gjq.train.business.trainseat.resp.TrainSeatQueryResp;
import com.gjq.train.business.trainseat.service.TrainSeatService;
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

class TrainSeatAdminControllerTests {

    private MockMvc mockMvc;

    private TrainSeatService trainSeatService;

    @BeforeEach
    void setUp() {
        trainSeatService = mock(TrainSeatService.class);
        TrainSeatAdminController controller = new TrainSeatAdminController();
        ReflectionTestUtils.setField(
                controller,
                "trainSeatService",
                trainSeatService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldSaveTrainSeat() throws Exception {
        mockMvc.perform(
                        post("/admin/train-seat/save")
                                .contentType(APPLICATION_JSON)
                                .content(seatJson(false))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainSeatService).save(argThat(
                (TrainSeatSaveReq request) ->
                        "G1".equals(request.getTrainCode())
                                && "01".equals(request.getRow())
        ));
    }

    @Test
    void shouldDeleteTrainSeat() throws Exception {
        mockMvc.perform(delete("/admin/train-seat/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainSeatService).delete(100L);
    }

    @Test
    void shouldUpdateTrainSeat() throws Exception {
        mockMvc.perform(
                        post("/admin/train-seat/update")
                                .contentType(APPLICATION_JSON)
                                .content(seatJson(true))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(trainSeatService).update(argThat(
                (TrainSeatUpdateReq request) ->
                        Long.valueOf(100L).equals(request.getId())
                                && "B".equals(request.getCol())
        ));
    }

    @Test
    void shouldQueryTrainSeatPage() throws Exception {
        PageResp<TrainSeatQueryResp> response = new PageResp<>();
        response.setTotal(0L);
        response.setList(List.of());
        when(trainSeatService.queryList(any(TrainSeatQueryReq.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/admin/train-seat/query-list")
                                .param("page", "1")
                                .param("size", "10")
                                .param("trainCode", "G1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.total").value(0))
                .andExpect(jsonPath("$.content.list").isArray());

        verify(trainSeatService).queryList(argThat(
                (TrainSeatQueryReq request) -> request.getPage() == 1
                        && request.getSize() == 10
                        && "G1".equals(request.getTrainCode())
        ));
    }

    private String seatJson(boolean includeId) {
        String id = includeId ? "\"id\": \"100\"," : "";
        return """
                {
                  %s
                  "trainCode": "G1",
                  "carriageIndex": 1,
                  "row": "01",
                  "col": "B",
                  "seatType": "2",
                  "carriageSeatIndex": 2
                }
                """.formatted(id);
    }
}
