package com.gjq.train.business.train.controller;

import com.gjq.train.business.train.resp.TrainQueryResp;
import com.gjq.train.business.train.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainControllerTests {

    @Test
    void shouldQueryAllTrains() throws Exception {
        TrainService trainService = mock(TrainService.class);
        TrainQueryResp train = new TrainQueryResp();
        train.setCode("G1");
        when(trainService.queryAll()).thenReturn(List.of(train));
        TrainController controller = new TrainController();
        ReflectionTestUtils.setField(controller, "trainService", trainService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/train/query-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("G1"));

        verify(trainService).queryAll();
    }
}
