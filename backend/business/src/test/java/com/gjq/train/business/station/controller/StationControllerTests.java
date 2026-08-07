package com.gjq.train.business.station.controller;

import com.gjq.train.business.station.resp.StationQueryResp;
import com.gjq.train.business.station.service.StationService;
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

class StationControllerTests {

    @Test
    void shouldQueryAllStations() throws Exception {
        StationService stationService = mock(StationService.class);
        StationQueryResp station = new StationQueryResp();
        station.setName("北京南");
        when(stationService.queryAll()).thenReturn(List.of(station));
        StationController controller = new StationController();
        ReflectionTestUtils.setField(
                controller,
                "stationService",
                stationService
        );
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/station/query-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("北京南"));

        verify(stationService).queryAll();
    }
}
